package de.taimos.maven_redmine_plugin;

/*
 * #%L redmine-maven-plugin Maven Mojo %% Copyright (C) 2012 - 2013 Taimos GmbH %% Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.taimos.maven_redmine_plugin.model.Version;

/**
 * Goal which closes the given version
 */
@Mojo(name = "rename-version")
public class RenameVersionMojo extends RedmineMojo {
	
	/**
	 * The version to rename
	 */
	@Parameter(defaultValue = "${project.version}", property = "renameVersion", required = true)
	private String renameVersion;
	
	/**
	 * The new name of the version
	 */
	@Parameter(defaultValue = "${project.version}", property = "newName", required = true)
	private String newName;
	
	
	@Override
	protected void doExecute() throws MojoExecutionException {
		final List<Version> versions = this.redmine.getVersions(this.getProjectIdentifier());
		for (final Version v : versions) {
			if (this.checkVersion(v)) {
				final String name = Version.createName(this.getProjectVersionPrefix(), Version.cleanSnapshot(this.newName));
				this.redmine.renameVersion(v, name);
				return;
			}
		}
		if (this.getProjectVersionPrefix().isEmpty()) {
			throw new MojoExecutionException(String.format("No version %s found for project %s.", Version.cleanSnapshot(this.renameVersion), this.getProjectIdentifier()));
		}
		throw new MojoExecutionException(String.format("No version %s-%s found for project %s.", this.getProjectVersionPrefix(), Version.cleanSnapshot(this.renameVersion), this.getProjectIdentifier()));
	}
	
	private boolean checkVersion(final Version v) {
		return v.getName().equals(Version.createName(this.getProjectVersionPrefix(), Version.cleanSnapshot(this.renameVersion)));
	}
	
}
