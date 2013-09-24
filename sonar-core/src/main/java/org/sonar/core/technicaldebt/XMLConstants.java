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

public final class XMLConstants {

  private XMLConstants(){
    // Utility class
  }

  public static final String CHARACTERISTIC = "chc";
  public static final String CHARACTERISTIC_KEY = "key";
  public static final String CHARACTERISTIC_NAME = "name";
  public static final String CHARACTERISTIC_DESCRIPTION = "desc";
  public static final String PROPERTY = "prop";
  public static final String PROPERTY_KEY = "key";
  public static final String PROPERTY_VALUE = "val";
  public static final String PROPERTY_TEXT_VALUE = "txt";
}
