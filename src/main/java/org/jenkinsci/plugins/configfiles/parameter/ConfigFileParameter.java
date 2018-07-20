package org.jenkinsci.plugins.configfiles.parameter;

import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.ConfigFiles;
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import java.util.List;

public class ConfigFileParameter extends ParameterDefinition  {

    ManagedFile managedFile;

    @DataBoundConstructor
    public ConfigFileParameter(String name, ManagedFile managedFile) {
        super(name);
        this.managedFile = managedFile;
    }

    public ManagedFile getManagedFile() {
        return managedFile;
    }

    public void setManagedFile(ManagedFile managedFile) {
        this.managedFile = managedFile;
    }

    public static List<Config> getAllConfigs() {
        return ConfigFiles.getConfigsInContext(Jenkins.getInstance(), null);
    }

    public static Config getConfigById(String id) {
        return ConfigFiles.getByIdOrNull(Jenkins.getInstance(), id);
    }

    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest request, JSONObject jsonObject) {
        return new ConfigFileParameterValue(jsonObject.getString("name"),
                new ManagedFile(jsonObject.getString("fileId"),
                    managedFile.getTargetLocation(),
                    managedFile.getVariable(),
                    managedFile.isReplaceTokens()));
    }

    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest request) {
        // Invoke via cgi query not supported
        return null;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        public String getDisplayName() {
            return "Config File Parameter";
        }

        @Override
        public ConfigFileParameter newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            JSONObject managedFileObject = (JSONObject) formData.get("managedFile");
            return new ConfigFileParameter(formData.getString("name"),
                    new ManagedFile(managedFileObject.getString("fileId"),
                            managedFileObject.getString("targetLocation"),
                            managedFileObject.getString("variable"),
                            managedFileObject.getBoolean("replaceTokens")));
        }

        public ListBoxModel doFillFileIdItems(@AncestorInPath ItemGroup context) {
            ListBoxModel items = new ListBoxModel();
            for (Config config : ConfigFiles.getConfigsInContext(context, null)) {
                items.add(config.name, config.id);
            }
            return items;
        }
    }
}
