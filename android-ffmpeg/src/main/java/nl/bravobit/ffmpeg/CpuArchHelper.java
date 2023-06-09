package nl.bravobit.ffmpeg;

import android.os.Build;

public class CpuArchHelper {
    private static final String X86_CPU = "x86";
    private static final String X86_64_CPU = "x86_64";
    private static final String ARM_64_CPU = "arm64-v8a";
    private static final String ARM_V7_CPU = "armeabi-v7a";

    public static CpuArch getCpuArch() {
        Log.d("Build.CPU_ABI : " + Build.CPU_ABI);

        switch (Build.CPU_ABI) {
            case X86_CPU:
                return CpuArch.x86;
            case X86_64_CPU:
                return CpuArch.x86_64;
            case ARM_64_CPU:
                return CpuArch.ARM64;
            case ARM_V7_CPU:
                return CpuArch.ARMv7;
            default:
                return CpuArch.NONE;
        }
    }
}
