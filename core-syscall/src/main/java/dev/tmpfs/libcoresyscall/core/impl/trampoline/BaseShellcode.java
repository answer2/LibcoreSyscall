package dev.tmpfs.libcoresyscall.core.impl.trampoline;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import dev.tmpfs.libcoresyscall.core.MemoryAccess;
import dev.tmpfs.libcoresyscall.core.NativeAccess;
import dev.tmpfs.libcoresyscall.core.NativeHelper;
import dev.tmpfs.libcoresyscall.core.Syscall;
import dev.tmpfs.libcoresyscall.core.impl.ByteArrayUtils;
import dev.tmpfs.libcoresyscall.core.impl.NativeBridge;
import dev.tmpfs.libcoresyscall.core.impl.ReflectHelper;
import dev.tmpfs.libcoresyscall.elfloader.SymbolResolver;

public abstract class BaseShellcode {

    protected BaseShellcode() {
    }

    public abstract int getNativeDebugBreakOffset();

    public abstract int getNativeClearCacheOffset();

    public abstract int getNativeSyscallOffset();

    public abstract int getNativeCallPointerFunction0Offset();

    public abstract int getNativeCallPointerFunction1Offset();

    public abstract int getNativeCallPointerFunction2Offset();

    public abstract int getNativeCallPointerFunction3Offset();

    public abstract int getNativeCallPointerFunction4Offset();

    public abstract int getNativeGetJavaVmOffset();

    public abstract int getFakeStat64Offset();

    public abstract int getFakeMmap64Offset();

    public abstract int getFakeMmapOffset();

    public abstract byte[] getShellcodeBytes();

    /**
     * Get the maximum number of arguments supported by the generated callPointerFunction shellcode.
     * Default is 4 (no dynamic entries). Architecture-specific implementations should override
     * to return 15 when they implement generateCallPointerFunctionNCode().
     *
     * @return the maximum N for nativeCallPointerFunctionN
     */
    public int getMaxCallPointerFunctionN() {
        return 4;
    }

    /**
     * Generate shellcode bytes for nativeCallPointerFunctionN with N arguments.
     * This is called for N from 5 to getMaxCallPointerFunctionN().
     * The generated code is appended to the trampoline page.
     *
     * @param n the number of arguments (5..getMaxCallPointerFunctionN())
     * @return the generated shellcode bytes
     */
    public byte[] generateCallPointerFunctionNCode(int n) {
        throw new UnsupportedOperationException(
                "generateCallPointerFunctionNCode not supported for N=" + n
                        + " on this architecture");
    }

    /**
     * Build the parameter types array for nativeCallPointerFunctionN reflection lookup.
     */
    @NonNull
    private static Class<?>[] buildCallPointerFunctionParamTypes(int n) {
        Class<?>[] paramTypes = new Class<?>[n + 1]; // function + n args
        paramTypes[0] = long.class;
        for (int i = 1; i <= n; i++) {
            paramTypes[i] = long.class;
        }
        return paramTypes;
    }

    /**
     * Get the ashmem dev_t id. If /dev/ashmem is not available, return 0.
     *
     * @return the ashmem dev_t id.
     */
    protected long getAshmemDeviceId() {
        StructStat statAshmem;
        try {
            statAshmem = Os.stat("/dev/ashmem");
        } catch (ErrnoException e) {
            if (e.errno == OsConstants.ENOENT) {
                return 0;
            } else {
                throw ReflectHelper.unsafeThrow(e);
            }
        }
        return statAshmem.st_dev;
    }

    /**
     * Get the __dl___errno function address.
     *
     * @return the __dl___errno function address.
     */
    protected long getDlErrnoFunctionAddress() {
        SymbolResolver linker;
        try {
            linker = SymbolResolver.getModule(NativeHelper.isCurrentRuntime64Bit() ? "linker64" : "linker");
        } catch (SymbolResolver.NoSuchModuleException | IOException e) {
            throw ReflectHelper.unsafeThrow(e);
        }
        long dl_errno = linker.getSymbolAddress("__dl___errno");
        if (dl_errno == 0) {
            throw new IllegalStateException("symbol not found: __dl___errno");
        }
        return dl_errno;
    }

    protected void fillInHookInfo(@NonNull byte[] shellcode, int offset) {
        ByteArrayUtils.writeInt64(shellcode, offset, getAshmemDeviceId());
        ByteArrayUtils.writeInt64(shellcode, offset + 8, getDlErrnoFunctionAddress());
        ByteArrayUtils.writeInt64(shellcode, offset + 16, NativeBridge.getPageSize());
    }

    public TrampolineInfo generateTrampoline() {
        final int pageSize = (int) NativeBridge.getPageSize();
        final byte[] shellcodeBytes = getShellcodeBytes();
        if (shellcodeBytes.length > pageSize) {
            throw new IllegalStateException("trampoline size is too large: " + shellcodeBytes.length);
        }
        byte[] trampolinePage = new byte[pageSize];
        System.arraycopy(shellcodeBytes, 0, trampolinePage, 0, shellcodeBytes.length);
        HashMap<Method, Integer> nativeEntryOffsetMap = new HashMap<>();
        try {
            // Register fixed methods (nativeSyscall, nativeClearCache, nativeGetJavaVM)
            nativeEntryOffsetMap.put(
                    NativeBridge.class.getMethod("nativeSyscall",
                            int.class, long.class, long.class, long.class, long.class, long.class, long.class),
                    getNativeSyscallOffset());
            nativeEntryOffsetMap.put(
                    NativeBridge.class.getMethod("nativeClearCache", long.class, long.class),
                    getNativeClearCacheOffset());
            nativeEntryOffsetMap.put(
                    NativeBridge.class.getMethod("nativeGetJavaVM"),
                    getNativeGetJavaVmOffset());

            // Register callPointerFunction0..4 from base shellcode (embedded in base64)
            int[][] fixedEntries = {
                    {0, getNativeCallPointerFunction0Offset()},
                    {1, getNativeCallPointerFunction1Offset()},
                    {2, getNativeCallPointerFunction2Offset()},
                    {3, getNativeCallPointerFunction3Offset()},
                    {4, getNativeCallPointerFunction4Offset()},
            };
            for (int[] entry : fixedEntries) {
                int n = entry[0];
                int offset = entry[1];
                nativeEntryOffsetMap.put(
                        NativeBridge.class.getMethod(
                                "nativeCallPointerFunction" + n,
                                buildCallPointerFunctionParamTypes(n)),
                        offset);
            }

            // Dynamically generate callPointerFunction5..getMaxCallPointerFunctionN()
            int nextOffset = shellcodeBytes.length;
            int maxN = getMaxCallPointerFunctionN();
            for (int n = 5; n <= maxN; n++) {
                byte[] code = generateCallPointerFunctionNCode(n);
                if (code == null) {
                    throw new UnsupportedOperationException(
                            "generateCallPointerFunctionNCode returned null for N=" + n);
                }
                if (nextOffset + code.length > pageSize) {
                    throw new IllegalStateException(
                            "trampoline page overflow at N=" + n
                                    + ": baseSize=" + shellcodeBytes.length
                                    + ", extraSize=" + (nextOffset - shellcodeBytes.length)
                                    + ", codeSize=" + code.length
                                    + ", pageSize=" + pageSize);
                }
                System.arraycopy(code, 0, trampolinePage, nextOffset, code.length);
                nativeEntryOffsetMap.put(
                        NativeBridge.class.getMethod(
                                "nativeCallPointerFunction" + n,
                                buildCallPointerFunctionParamTypes(n)),
                        nextOffset);
                nextOffset += code.length;
            }
        } catch (NoSuchMethodException e) {
            throw ReflectHelper.unsafeThrow(e);
        }
        return new TrampolineInfo(trampolinePage, nativeEntryOffsetMap);
    }

    /**
     * Write the shellcode to the text section.
     * <p>
     * If the destination address is not text section, the result is undefined.
     *
     * @param shellcode the shellcode bytes to write
     * @param address   the address to write the shellcode
     */
    protected void writeByteArrayToTextSection(@NonNull byte[] shellcode, long address) {
        if (!NativeHelper.isCurrentRuntime64Bit() && (address & 0xffffffff00000000L) != 0) {
            throw new IllegalArgumentException("address overflow");
        }
        final int pageSize = (int) MemoryAccess.getPageSize();
        try {
            long pageStart = ByteArrayUtils.alignDown(address, pageSize);
            long pageEnd = ByteArrayUtils.alignUp(address + shellcode.length, pageSize);
            Syscall.mprotect(pageStart, pageEnd - pageStart,
                    OsConstants.PROT_READ | OsConstants.PROT_EXEC | OsConstants.PROT_WRITE);
            MemoryAccess.pokeByteArray(address, shellcode, 0, shellcode.length);
            Syscall.mprotect(pageStart, pageEnd - pageStart, OsConstants.PROT_READ | OsConstants.PROT_EXEC);
            NativeAccess.clearCache(address, shellcode.length);
        } catch (ErrnoException e) {
            throw ReflectHelper.unsafeThrow(e);
        }
    }

}
