/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.core.technicaldebt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import org.picocontainer.Startable;
import org.sonar.api.ServerExtension;
import org.sonar.api.platform.PluginMetadata;
import org.sonar.api.platform.PluginRepository;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * <p>This class is used to find which technical debt model XML files exist in the Sonar instance.</p>
 * <p>
 * Those XML files are provided by language plugins that embed their own contribution to the definition of the Technical debt model.
 * They must be located in the classpath of those language plugins, more specifically in the "com.sonar.sqale" package, and
 * they must be named "<pluginKey>-model.xml".
 * </p>
 */
public class TechnicalDebtModelFinder implements ServerExtension, Startable {

  public static final String DEFAULT_MODEL = "technical-debt";

  private static final String XML_FILE_SUFFIX = "-model.xml";
  private static final String XML_FILE_PREFIX = "com/sonar/sqale/";

  private String xmlFilePrefix;

  private PluginRepository pluginRepository;
  private Map<String, ClassLoader> contributingPluginKeyToClassLoader;

  /**
   *
   * @param pluginRepository
   */
  public TechnicalDebtModelFinder(PluginRepository pluginRepository) {
    this.pluginRepository = pluginRepository;
    this.xmlFilePrefix = XML_FILE_PREFIX;
  }

  @VisibleForTesting
  TechnicalDebtModelFinder(PluginRepository pluginRepository, String xmlFilePrefix) {
    this.pluginRepository = pluginRepository;
    this.xmlFilePrefix = xmlFilePrefix;
  }

  @VisibleForTesting
  TechnicalDebtModelFinder(Map<String, ClassLoader> contributingPluginKeyToClassLoader, String xmlFilePrefix) {
    this.contributingPluginKeyToClassLoader = contributingPluginKeyToClassLoader;
    this.xmlFilePrefix = xmlFilePrefix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    findAvailableXMLFiles();
  }

  protected void findAvailableXMLFiles() {
    if (contributingPluginKeyToClassLoader == null) {
      contributingPluginKeyToClassLoader = Maps.newTreeMap();
      for (PluginMetadata metadata : pluginRepository.getMetadata()) {
        String pluginKey = metadata.getKey();
        ClassLoader classLoader = pluginRepository.getPlugin(pluginKey).getClass().getClassLoader();
        if (classLoader.getResource(getXMLFilePathForPlugin(pluginKey)) != null) {
          contributingPluginKeyToClassLoader.put(pluginKey, classLoader);
        }
      }
    }
    contributingPluginKeyToClassLoader = Collections.unmodifiableMap(contributingPluginKeyToClassLoader);
  }

  protected String getXMLFilePathForPlugin(String pluginKey) {
    return xmlFilePrefix + pluginKey + XML_FILE_SUFFIX;
  }

  /**
   * Returns the list of plugins that can contribute to the SQALE model (without the default model provided by this plugin).
   *
   * @return the list of plugin keys
   */
  public Collection<String> getContributingPluginList() {
    Collection<String> contributingPlugins = newArrayList(contributingPluginKeyToClassLoader.keySet());
    contributingPlugins.remove(DEFAULT_MODEL);
    return contributingPlugins;
  }

  /**
   * Creates a new {@link java.io.Reader} for the XML file that contains the model contributed by the given plugin.
   * 
   * @param pluginKey the key of the plugin that contributes the XML file
   * @return the reader, that must be closed once its use is finished.
   */
  public Reader createReaderForXMLFile(String pluginKey) {
    ClassLoader classLoader = contributingPluginKeyToClassLoader.get(pluginKey);
    String xmlFilePath = getXMLFilePathForPlugin(pluginKey);
    return new InputStreamReader(classLoader.getResourceAsStream(xmlFilePath));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    // Nothing to do
  }

}
