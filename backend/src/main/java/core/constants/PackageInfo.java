package core.constants;

import core.MyLogger;

/**
 * Used primarily as a struct to hold the app info that was pulled from the XML file. This class is immutable.
 */
public class PackageInfo
{
    public final String id; // not currently using ID for anything, maybe we can use it in the future?
    public final String appName;
    public final String packageName;
    public final String engCode;
    public final String copyLogs;


    public PackageInfo(String id, String appName, String packageName, String engCode, String copyLogs)
    {
        this.id = (id.isEmpty()) ? null : id;
        this.appName = (appName.isEmpty()) ? null : appName;
        this.packageName = (packageName.isEmpty()) ? null : packageName.toLowerCase();
        this.engCode = (engCode.isEmpty()) ? null : engCode;
        this.copyLogs = (copyLogs.isEmpty()) ? null : copyLogs;
    }

    /**
     * Checks if the package name this struct holds is the same as the specified package name.
     * @param packageName The name of the package to check (i.e. com.att.eptt)
     * @return Returns true if package name is the same as the package name in this struct. This
     * function will also exclude package names that contains the word "test" in it.
     */
    public boolean validatePackageName(String packageName)
    {
        if (packageName == null)
        {
            MyLogger.log.debug("packageName is null!");
            return false;
        }

        packageName = packageName.toLowerCase();
        return this.packageName.contains(packageName) && !packageName.contains("test");
    }

    /**
     * Checks if the package provided to other is the same as this one. This will check all properties to see if they
     * are the same or not.
     * @param other The package to compare to
     * @return True if the package is the same.
     */
    public boolean equals(PackageInfo other)
    {
        return id.equals(other.id) && appName.equals(other.appName)
            && packageName.equals(other.packageName) && engCode.equals(other.engCode);
    }

    /**
     * Does every property have a valid value?
     * @return True if this class contains a valid values for the apps.
     */
    public boolean isValid()
    {
        return id != null && appName != null && packageName != null && engCode != null;
    }
}
