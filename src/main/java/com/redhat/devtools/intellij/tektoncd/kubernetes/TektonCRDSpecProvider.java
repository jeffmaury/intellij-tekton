/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.intellij.tektoncd.kubernetes;

import com.intellij.kubernetes.KubernetesCRDSpecProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.redhat.devtools.intellij.tektoncd.validation.TektonSchemasProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TektonCRDSpecProvider implements KubernetesCRDSpecProvider {
    private static final String[] FILES = {
       "tekton.dev_clustertasks.yaml",
       "tekton.dev_conditions.yaml",
       "tekton.dev_pipelineresources.yaml",
       "tekton.dev_pipelineruns.yaml",
       "tekton.dev_pipelines.yaml",
       "tekton.dev_runs.yaml",
       "tekton.dev_taskruns.yaml",
       "tekton.dev_tasks.yaml",
       "triggers.tekton.dev_clustertriggerbindings.yaml",
       "triggers.tekton.dev_eventlisteners.yaml",
       "triggers.tekton.dev_triggerbindings.yaml",
       "triggers.tekton.dev_triggers.yaml",
       "triggers.tekton.dev_triggertemplates.yaml"
    };

    private final List<VirtualFile> tektonCRDs = new ArrayList<>();

    public TektonCRDSpecProvider() {
        for(String file : FILES) {
            tektonCRDs.add(loadCRD(file));
        }
    }

    private VirtualFile loadCRD(String crdFile) {
        URL url = TektonSchemasProviderFactory.class.getResource("/crds/" + crdFile);
        if (url != null) {
            return VirtualFileManager.getInstance().refreshAndFindFileByUrl(VfsUtil.convertFromUrl(url));
        }
        return null;
    }

    @Override
    public @NotNull List<VirtualFile> getCRDSpecs(@NotNull Project project) {
        return tektonCRDs;
    }
}
