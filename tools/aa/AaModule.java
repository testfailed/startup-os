/*
 * Copyright 2018 The StartupOS Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.startupos.tools.aa;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.tools.aa.Protos.Config;
import com.google.startupos.tools.aa.commands.InitCommand;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;


@Module(includes = CommonModule.class)
public class AaModule {
  @Inject
  AaModule() {}

  @Provides @Named("Base path") static String provideBasePath(FileUtils fileUtils) {
    String currentFolder = fileUtils.getCurrentWorkingDirectory();
    while (currentFolder != null) {
      if (fileUtils.fileExists(fileUtils.joinPaths(currentFolder, InitCommand.BASE_FILENAME))) {
        return currentFolder;
      }
      File file = new File(currentFolder);
      currentFolder = file.getAbsoluteFile().getParent();
    }
    throw new IllegalStateException("BASE file not found in path until root");
  }

  @Provides
  public static Config getConfig(FileUtils fileUtils, @Named("Base path") String basePath) {
    Config config = (Config) fileUtils.readPrototxtUnchecked(
        fileUtils.joinPaths(basePath, InitCommand.BASE_FILENAME),
        Config.newBuilder());
    config = config.toBuilder().setBasePath(basePath).build();
    return config;
  }
}
