/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.intellij.tektoncd.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable  implements Configurable {

    private SettingsView mySettingsView;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Tekton Pipelines by Red Hat";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsView = new SettingsView();
        return mySettingsView.getPanel();
    }

    @Override
    public boolean isModified() {
        SettingsState settings = SettingsState.getInstance();
        boolean modified = mySettingsView.getDisplayPipelineRunResultAsNotification() != settings.displayPipelineRunResultAsNotification;
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        SettingsState settings = SettingsState.getInstance();
        settings.displayPipelineRunResultAsNotification = mySettingsView.getDisplayPipelineRunResultAsNotification();
    }

    @Override
    public void reset() {
        SettingsState settings = SettingsState.getInstance();
        mySettingsView.setDisplayPipelineRunResultAsNotification(settings.displayPipelineRunResultAsNotification);
    }

    @Override
    public void disposeUIResources() {
        mySettingsView = null;
    }
}