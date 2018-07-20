package org.jenkinsci.plugins.configfiles.parameter;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.StringParameterValue;
import hudson.tasks.BuildWrapper;
import hudson.util.VariableResolver;
import jenkins.model.Jenkins;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.lib.configprovider.model.ConfigFile;
import org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper;
import org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.jenkinsci.plugins.configfiles.parameter.ConfigFileParameter.getConfigById;

public class ConfigFileParameterValue extends StringParameterValue {
    ManagedFile managedFile;

    public ConfigFileParameterValue(String name, ManagedFile managedFile) {
        super(name, managedFile.getFileId());
        this.managedFile = managedFile;
    }

    public String getConfigName() {
        return getConfigById(managedFile.getFileId()).name;
    }

    public ManagedFile getManagedFile() {
        return managedFile;
    }

    public void setManagedFile(ManagedFile managedFile) {
        this.managedFile = managedFile;
    }

    public String getViewFileLink() {
        String link = Jenkins.getInstance().getRootUrl();
        link = link + "configfiles/show?id=" + managedFile.getFileId();
        return link;
    }

    @Override
    public VariableResolver<String> createVariableResolver(AbstractBuild<?, ?> build) {
        return VariableResolver.NONE;
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
        List<ManagedFile> managedFiles = new ArrayList<>();
        managedFiles.add(managedFile);
        return new ConfigFileBuildWrapper(managedFiles);
    }
}
