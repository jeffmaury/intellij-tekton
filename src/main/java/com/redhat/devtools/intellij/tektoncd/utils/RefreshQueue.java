/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.intellij.tektoncd.utils;

import com.intellij.ui.treeStructure.Tree;
import com.redhat.devtools.intellij.common.actions.StructureTreeAction;
import com.redhat.devtools.intellij.tektoncd.Constants;
import com.redhat.devtools.intellij.tektoncd.listener.TreePopupMenuListener;
import com.redhat.devtools.intellij.tektoncd.tree.ParentableNode;
import com.redhat.devtools.intellij.tektoncd.tree.TektonTreeStructure;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.tree.TreePath;

public class RefreshQueue {
    private static RefreshQueue instance;
    private Queue<TreePath> queue;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scheduler;

    private RefreshQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    public static RefreshQueue get() {
        if (instance == null) {
            instance = new RefreshQueue();
        }
        return instance;
    }

    public void addAll(List<TreePath> nodes) {
        if (scheduler != null && !scheduler.isCancelled() && !scheduler.isDone()) {
            scheduler.cancel(true);
        }

        for (TreePath node: nodes) {
            if (isValid(node)) {
                queue.removeIf(element -> element.getParentPath().equals(node.getParentPath()));
                queue.add(node);
            }
        }

        scheduler = executor.schedule(() -> {
            if (!TreePopupMenuListener.isTreeMenuVisible()) {
                update();
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    private boolean isValid(TreePath node) {
        // if node is a parent of one element in the queue, remove that element
        queue.removeIf(element -> node.isDescendant(element));

        // if node is a descendant does not have to be added, a parent will be refreshed soon
        boolean isDescendant = queue.stream().anyMatch(element -> element.isDescendant(node));
        if (isDescendant) {
            return false;
        }

        return true;
    }

    public void update() {
        while (!queue.isEmpty()) {
            TreePath treePath = queue.poll();
            if (!isValid(treePath)) return;
            ParentableNode element = StructureTreeAction.getElement(treePath.getLastPathComponent());
            Tree tree = TreeHelper.getTree(element.getRoot().getProject());
            TektonTreeStructure treeStructure = (TektonTreeStructure) tree.getClientProperty(Constants.STRUCTURE_PROPERTY);
            boolean isExpanded = tree.isExpanded(treePath);
            if (isExpanded) {
                treeStructure.fireModified(element);
            }
        }
    }

}
