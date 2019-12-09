/**
 * Copyright (c) 2019, Salesforce.com, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.bazel.eclipse.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.salesforce.bazel.eclipse.model.BazelPackageInfo;
import com.salesforce.bazel.eclipse.test.TestBazelWorkspaceFactory;

public class BazelProjectImportScannerTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testHappyPath() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        File tmpOutputBase = tmpFolder.newFolder();
        
//        File tmpWorkspaceDir = new File("/tmp/bazeltest/ws");
//        File tmpOutputBase = new File("/tmp/bazeltest/bin");
        
        
        new TestBazelWorkspaceFactory(tmpWorkspaceDir, tmpOutputBase).javaPackages(5).genrulePackages(2).build();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(5, rootWorkspacePackage.getChildPackageInfos().size());
    }

    // UNHAPPY PATHS
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDirectory() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        scanner.getProjects(tmpWorkspaceDir);
    }

    @Test
    public void testNoJavaProjects() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        File tmpOutputBase = tmpFolder.newFolder();
        new TestBazelWorkspaceFactory(tmpWorkspaceDir, tmpOutputBase).javaPackages(0).genrulePackages(2).build();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(0, rootWorkspacePackage.getChildPackageInfos().size());
    }
    
    @Test
    public void testNoProjects() throws Exception {
        File tmpWorkspaceDir = tmpFolder.newFolder();
        File workspaceFile = new File(tmpWorkspaceDir, "WORKSPACE");
        workspaceFile.createNewFile();
        
        BazelProjectImportScanner scanner = new BazelProjectImportScanner();
        BazelPackageInfo rootWorkspacePackage = scanner.getProjects(tmpWorkspaceDir);
        
        assertEquals(0, rootWorkspacePackage.getChildPackageInfos().size());
    }
}
