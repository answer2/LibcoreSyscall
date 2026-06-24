package dev.tmpfs.libcoresyscall.core.impl.arch;

import dev.tmpfs.libcoresyscall.core.impl.ByteArrayUtils;
import dev.tmpfs.libcoresyscall.core.impl.trampoline.BaseShellcode;
import dev.tmpfs.libcoresyscall.core.impl.trampoline.ISimpleInlineHook;
import dev.tmpfs.libcoresyscall.core.impl.trampoline.ISyscallNumberTable;

public class ShellcodeImpl_Riscv64 extends BaseShellcode implements ISimpleInlineHook, ISyscallNumberTable {

    public static final ShellcodeImpl_Riscv64 INSTANCE = new ShellcodeImpl_Riscv64();

    private ShellcodeImpl_Riscv64() {
        super();
    }

    @Override
    public byte[] getShellcodeBytes() {
        //0000 g    DF .text  0004 NativeBridge_breakpoint
        //0000 g    D  .text  0000 ___text_section
        //0004 g    DF .text  0012 NativeBridge_nativeSyscall
        //0016 g    DF .text  0014 syscall_ext
        //002a g    DF .text  0016 NativeBridge_nativeClearCache
        //0040 g    DF .text  0010 __clear_cache
        //0050 g    DF .text  0002 NativeBridge_nativeCallPointerFunction0
        //0052 g    DF .text  0004 NativeBridge_nativeCallPointerFunction1
        //0056 g    DF .text  0006 NativeBridge_nativeCallPointerFunction2
        //005c g    DF .text  000a NativeBridge_nativeCallPointerFunction3
        //0066 g    DF .text  000c NativeBridge_nativeCallPointerFunction4
        //0072 g    DF .text  0030 NativeBridge_nativeGetJavaVM
        //00a2 g    DF .text  0014 ashmem_dev_get_size_region
        //00b6 g    DF .text  000a get_hook_info
        //00c0 g    DF .text  0004 get_current_pc
        //00c4 g    DF .text  008c fake_fstat64
        //0150 g    DF .text  02b0 fake_mmap64
        //0480 g    DF .text  0002 fake_mmap
        //0750 l     O .rodata  0018 _ZZ13get_hook_infoE9sHookInfo
        String b64 =
                "ApCCgIJitoUyhTqGvoZCh8aHFogJoKqILoWyhTaGuoY+h8KHcwAAAIKAMoWzhcYAkwgwEAFGcwAA\n" +
                        "ABHhgoAAAJMIMBABRnMAAAAR4YKAAAAChjaFAoa6hTaFAoa6hTaFMoc+hgKHuoU2hTKHPobChgKH\n" +
                        "AREG7CLoABAMYQO2hW0jNAT+kwWE/gKWgzWE/jM1oAB9FW2NEwEE/uJgQmQFYYKAqoUdZRsGRXB1\n" +
                        "RYFGAUeBRwFIjbcXBQAAEwWlaYKABoWCgHlxBvQi8CbsSuhO5AAYrokqiRMFAAXKhU6GgUYBR4FH\n" +
                        "AUjv8B/z/XWFJWNttQCqhBcFAAADNUVmApW7BZBADMF9VS2gA7YJABcFAACDNUVkAUVjHbYAmckD\n" +
                        "tQkDGeVKhe/w//d9dmN8pgABRRMBBP2icAJ04mRCaaJpRWGCgKqFAUUjuLkCI7QJAM23SXGG9qLy\n" +
                        "pu7K6s7m0uJW/lr6XvZi8mbuaupu5oAavou6iTaLMokuiqqKgUyFTRcFAAATDCVdY00HDhN1KwKJ\n" +
                        "RWMYtQ5XcLjNVzQAXhMFBPEndAUCEwUABRMGBPHOhYFGAUeBRwFI7/Af5v11Y/ylAKqEAzWMAAKV\n" +
                        "gUy7BZBADMEFRW2ggzUE8QM1DABjkKUCEc0DNQT0GelOhe/wn+v9dWPmpQAjMKT0IzwE8AM1BPGD\n" +
                        "NQwALY0TNRUAszWwALN8tQCTdUkABUW1wRMFBO9XcIHNVzQAXid0BQITBQTuJ3QFAhMFBO0ndAUC\n" +
                        "V3CkzVc0AF4TBQTpJ3QFAiMwBPATBcACEwYE6c6FgUYBR4FHAUjv8H/bgzUE6TsFBQg3JgIBGwZG\n" +
                        "mbGNTY0zNaAAs32VQQM1jAAClZOVSwPZRPHlKo0TlRwAs2YlARMF4A3WhVKGWofOh16I7/Af1/11\n" +
                        "hSVjabUCmwUFALUFszWwABNG+/8FgpNWWwATR/n/CYPZjlWOBYozZrYB0Y29xbsEoEClqGOODAaq\n" +
                        "imMKCgIFa/FcVo3ShCmgiYyqmyqdhcCz1mQLEwUwBM6FaoZeh4FHAUjv8J/Q40Gg/uMClf8DNQwB\n" +
                        "fRqzBaoAMwagQG2OEwUgDtaFyoYBR4FHAUjv8P/NE3VJAB3tVoUhqBN1KwATNRUAM2W1AbVEOcVq\n" +
                        "hQTBfVUTAQTptnAWdPZkVmm2aRZq8npSe7J7EnzybFJtsm11YYKAgzUMATMGsEAzdVYBVprSlfGN\n" +
                        "kwgwEAFGcwAAAKqFVoXd3bmok3a5/xMF4A3WhVKGWofOh16I7/A/xv18qoVqhePtvPgTBXANUoaB\n" +
                        "RgFHgUcBSO/wf8QBJYUsY3OVA5NmKQATZwsCEwXgDf1X1oVShgFI7/B/wv11hSXjcbXu5bUAAEER\n" +
                        "BuQi4AAILoaqhRMFwAKBRgFHgUcBSO/w/78BJRMBBP+iYAJkQQGCgCqIEwXgDb6Iuoc2h7KGLobC\n" +
                        "hUaI4b4qhxMFMAS2h7KGLoa6hT6HgUcBSMm2QREG5CLgAAiyhi6GqoUTBSAOAUeBRwFI7/CfugEl\n" +
                        "EwEE/6JgAmRBAYKAwblBEQbkIuAACC6GqoUTBXANgUYBR4FHAUjv8N+3ASUTAQT/omACZEEBgoCq\n" +
                        "hhMF8AMyhy6GtoW6hgFHgUcBSJm+qoYTBQAEMocuhraFuoYBR4FHAUiBtkERBuQi4AAIsocuhqqF\n" +
                        "O4cGCBMFgAO+hoFHAUjv8D+yASUTAQT/omACZEEBgoBBEQbkIuAACDaHsoYuhqqFEwXwBIFHAUjv\n" +
                        "8J+vASUTAQT/omACZEEBgoBBEQbkIuAACK6GKoYTBfAEkwXA+QFHgUcBSO/w36wBJRMBBP+iYAJk\n" +
                        "QQGCgEERBuQi4AAILoaqhRMFAAWBRgFHgUcBSO/wP6oBJRMBBP+iYAJkQQGCgEERBuQi4AAIqoUT\n" +
                        "BZADAUaBRgFHgUcBSO/wn6cBJRMBBP+iYAJkQQGCgEERBuQi4AAIsoYuhqqFdUUBR4FHAUjv8B+l\n" +
                        "ASUTAQT/omACZEEBgoBBEQbkIuAACKqFEwXgBQFGgUYBR4FHAUjv8H+iAABjBgYUIwC1ALMGxQAN\n" +
                        "R6OPtv5jbeYSowC1ACMBtQAjj7b+HUejjrb+Y2LmEqMBtQAlRyOOtv5ja+YQuwagQJP3NgCzBvUA\n" +
                        "Mwj2QBN2yP/iFbcXEBCSB5OHBxCztfUCjMKzh8YAI663/mNi5g7MwozGI6q3/mVHI6y3/mNp5gzM\n" +
                        "xozKzMqMziOit/4jpLf+I6a3/hP3RgCTCIcBswIWQRMGAAIjqLf+Y+PCChOWBQK7hcUIMwboQBMG\n" +
                        "hvwVghMIFgBzJiDCE18mALOHFgFjdOgBvoaJqLMG4EGzeNgAk5ZYALOC0kC+lld3kA1XxAVeDgZX\n" +
                        "pQhSV7WilhMDAAKhQ0FO4U5Gh1fGpwIn9GcKJ/TDDid0zg4n9M4OMwfnQbKXffNjBhgDE4YC/n1H\n" +
                        "M1bmChNG9v8WlgGaNpYTBgYCjOKM5ozqjO6ThgYC45rG/oKAAAAAAAAAAAAAAAAA776v3gAAAAAU\n" +
                        "RREAAAAAAAAQAAAAAAAA\n";
        byte[] bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
        int hookInfoOffset = 0x0750;
        fillInHookInfo(bytes, hookInfoOffset);
        return bytes;
    }

    @Override
    public int getNativeDebugBreakOffset() {
        return 0x0000;
    }

    @Override
    public int getNativeClearCacheOffset() {
        return 0x002a;
    }

    @Override
    public int getNativeSyscallOffset() {
        return 0x0004;
    }

    @Override
    public int getNativeCallPointerFunction0Offset() {
        return 0x0050;
    }

    @Override
    public int getNativeCallPointerFunction1Offset() {
        return 0x0052;
    }

    @Override
    public int getNativeCallPointerFunction2Offset() {
        return 0x0056;
    }

    @Override
    public int getNativeCallPointerFunction3Offset() {
        return 0x005c;
    }

    @Override
    public int getNativeCallPointerFunction4Offset() {
        return 0x0066;
    }

    @Override
    public int getMaxCallPointerFunctionN() {
        return 15;
    }

    /**
     * Generate RV64 shellcode for nativeCallPointerFunctionN with N arguments (N >= 5).
     *
     * RV64 ABI:
     *   a0-a7 (x10-x17): first 8 integer args
     *   sp: stack pointer, additional args at positive sp offsets
     * JNI entry: a0=env, a1=jclass, a2=func, a3=arg1, a4=arg2, a5=arg3, a6=arg4, a7=arg5
     *   [sp+0]=arg6, [sp+8]=arg7, [sp+16]=arg8, [sp+24]=arg9, ...
     * Target: a0=arg1..a7=arg8, [sp+0]=arg9, [sp+8]=arg10, ...
     */
    @Override
    public byte[] generateCallPointerFunctionNCode(int n) {
        if (n < 5 || n > 15) return null;
        // Compute instruction count
        int count = 7; // mv t0,a2; mv a0,a3; mv a1,a4; mv a2,a5; mv a3,a6; mv a4,a7; jr t0
        if (n >= 6) count++; // ld a5, 0(sp)
        if (n >= 7) count++; // ld a6, 8(sp)
        if (n >= 8) count++; // ld a7, 16(sp)
        if (n > 8) count += (n - 8) * 2; // ld+sd pairs for arg9..argN
        int[] insns = new int[count];
        int i = 0;

        // Register numbers: a0=10, a1=11, a2=12, a3=13, a4=14, a5=15, a6=16, a7=17
        // t0=5, t1=6, sp=2
        // addi rd, rs1, 0  →  mv rd, rs1: (rs1 << 15) | (rd << 7) | 0x13
        // ld rd, offset(sp): (offset << 20) | (2 << 15) | (3 << 12) | (rd << 7) | 0x03
        // sd rs2, offset(sp): ((offset>>5)<<25) | (rs2<<20) | (2<<15) | (3<<12) | ((offset&0x1F)<<7) | 0x23
        // jr rs1: (rs1 << 15) | 0x67

        // save function pointer (a2=x12 → t0=x5)
        insns[i++] = (12 << 15) | (5 << 7) | 0x13; // mv t0, a2

        // move register args to target positions
        insns[i++] = (13 << 15) | (10 << 7) | 0x13; // mv a0, a3  (arg1)
        insns[i++] = (14 << 15) | (11 << 7) | 0x13; // mv a1, a4  (arg2)
        insns[i++] = (15 << 15) | (12 << 7) | 0x13; // mv a2, a5  (arg3)
        insns[i++] = (16 << 15) | (13 << 7) | 0x13; // mv a3, a6  (arg4)
        insns[i++] = (17 << 15) | (14 << 7) | 0x13; // mv a4, a7  (arg5)

        // load stack register args (arg6..arg8)
        if (n >= 6) insns[i++] = (0 << 20) | (2 << 15) | (3 << 12) | (15 << 7) | 0x03;  // ld a5, 0(sp)
        if (n >= 7) insns[i++] = (8 << 20) | (2 << 15) | (3 << 12) | (16 << 7) | 0x03;  // ld a6, 8(sp)
        if (n >= 8) insns[i++] = (16 << 20) | (2 << 15) | (3 << 12) | (17 << 7) | 0x03; // ld a7, 16(sp)

        // shuffle extra stack args (arg9..argN)
        // JNI: argK at [sp + (K-6)*8], target: argK at [sp + (K-9)*8]
        for (int k = 9; k <= n; k++) {
            int srcOff = (k - 6) * 8;
            int dstOff = (k - 9) * 8;
            // ld t1, srcOff(sp)  → load argK
            insns[i++] = (srcOff << 20) | (2 << 15) | (3 << 12) | (6 << 7) | 0x03;
            // sd t1, dstOff(sp)  → store to target slot
            insns[i++] = ((dstOff >> 5) << 25) | (6 << 20) | (2 << 15) | (3 << 12)
                    | ((dstOff & 0x1F) << 7) | 0x23;
        }

        // jr t0  — call function (ra preserved → JNI return path)
        insns[i++] = (5 << 15) | 0x67;

        // Encode instructions to byte array
        byte[] result = new byte[insns.length * 4];
        for (int j = 0; j < insns.length; j++) {
            int insn = insns[j];
            int off = j * 4;
            result[off] = (byte) (insn & 0xFF);
            result[off + 1] = (byte) ((insn >> 8) & 0xFF);
            result[off + 2] = (byte) ((insn >> 16) & 0xFF);
            result[off + 3] = (byte) (insn >>> 24);
        }
        return result;
    }

    @Override
    public int getNativeGetJavaVmOffset() {
        return 0x0072;
    }

    @Override
    public int getFakeStat64Offset() {
        return 0x00c4;
    }

    @Override
    public int getFakeMmap64Offset() {
        return 0x0150;
    }

    @Override
    public int getFakeMmapOffset() {
        return 0x0480;
    }

    @Override
    public int __NR_mprotect() {
        // mprotect riscv64 226
        return 226;
    }

    @Override
    public int __NR_memfd_create() {
        // memfd_create riscv64 279
        return 279;
    }

    @Override
    public int __NR_ioctl() {
        // ioctl riscv64 29
        return 29;
    }

    @Override
    public int __NR_tgkill() {
        // tgkill riscv64 131
        return 131;
    }

    @Override
    public void inlineHook(long address, long hook) {
        if (address == 0) {
            throw new IllegalArgumentException("address is 0");
        }
        if (hook == 0) {
            throw new IllegalArgumentException("hook is 0");
        }
        if (address % 2 != 0 || hook % 2 != 0) {
            throw new IllegalArgumentException("address or hook is not aligned, address: " + address + ", hook: " + hook);
        }
        int nopCount = ((((int) address % 8) + 8 + 4) % 8) % 2;
        // 01 00         nop
        // nopCount * 2 + 12 + 8
        int nopBytes = nopCount * 2;
        byte[] trampoline = new byte[nopBytes + 20];
        for (int i = 0; i < nopBytes; i += 2) {
            trampoline[i] = 0x01;
            trampoline[i + 1] = 0x00;
        }
        // add jump to hook
        // 17 0e 00 00   auipc   t3, 0x0
        // 03 3e ce 00   ld      t3, 0xc(t3)
        // 67 03 0e 00   jalr    t1, t3
        // .addr hook
        ByteArrayUtils.writeBytes(trampoline, nopBytes, new byte[]{
                0x17, 0x0e, 0x00, 0x00,
                0x03, 0x3e, (byte) 0xce, 0x00,
                0x67, 0x03, 0x0e, 0x00
        });
        ByteArrayUtils.writeInt64(trampoline, nopBytes + 12, hook);
        writeByteArrayToTextSection(trampoline, address);
    }

}
