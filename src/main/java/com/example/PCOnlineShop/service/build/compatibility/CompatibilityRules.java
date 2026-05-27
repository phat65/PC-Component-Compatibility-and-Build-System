package com.example.PCOnlineShop.service.build.compatibility;

final class CompatibilityRules {

    private static final java.util.regex.Pattern PCIE_VERSION_PATTERN =
            java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)");

    private CompatibilityRules() {
    }

    static double parsePcieVersion(String version) {
        if (version == null) {
            return 0.0;
        }
        java.util.regex.Matcher matcher = PCIE_VERSION_PATTERN.matcher(version);
        if (!matcher.find()) {
            return 0.0;
        }

        return Double.parseDouble(matcher.group(1));
    }

    static boolean isCaseFormFactorCompatible(String caseFormFactor, String mbFormFactor) {
        if (caseFormFactor == null || mbFormFactor == null) {
            return true;
        }

        String caseFF = caseFormFactor.toUpperCase().trim();
        String mbFF = mbFormFactor.toUpperCase().trim();

        if (caseFF.contains("MINI") || caseFF.contains("ITX")) {
            return mbFF.contains("MINI");
        }

        if (caseFF.contains("MICRO")) {
            return mbFF.contains("MICRO") || mbFF.contains("MINI");
        }

        if (caseFF.contains("ATX")) {
            return true;
        }

        return caseFF.equals(mbFF);
    }

    static boolean isPsuFormFactorCompatible(String caseFormFactor, String psuFormFactor) {
        if (caseFormFactor == null || psuFormFactor == null) {
            return true;
        }

        String caseFF = caseFormFactor.toUpperCase().trim();
        String psuFF = psuFormFactor.toUpperCase().trim();

        if (caseFF.contains("ATX")) {
            return true;
        }

        if (caseFF.contains("SFX-L")) {
            return psuFF.contains("SFX") || psuFF.contains("TFX");
        }

        if (caseFF.contains("SFX")) {
            return psuFF.contains("SFX") || psuFF.contains("TFX");
        }

        if (caseFF.contains("TFX")) {
            return psuFF.contains("TFX");
        }

        return caseFF.equals(psuFF);
    }
}
