/* Copyright 2019 Tanaka Akira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fsij.pdftimestamp;

import java.io.File;
import java.io.IOException;
import java.io.Console;
import java.security.Security;
import org.apache.log4j.BasicConfigurator;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

/*
 * Usage:
 *   pdftimestamp [-p PDF-PASSWORD] TSA-URL INPUT-PDF OUTPUT-PDF
 */

public class PDFTimeStamp {
  public static void main(String[] args)
  throws ParseException, IOException {

    BasicConfigurator.configure();

    Options options = new Options();

    options.addOption("h", "help", false, "show help message");
    options.addOption("p", true, "PDF password");
    options.addOption(null, "ask-password", false, "ask PDF password interactively");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    if (cmd.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("pdftimestamp [options] input.pdf output.pdf", options);
      System.exit(0);
    }

    String password = cmd.getOptionValue("p");

    if (cmd.hasOption("ask-password")) {
      Console console = System.console();
      char[] passwordArray = console.readPassword("Enter PDF password:");
      password = new String(passwordArray);
    }

    args = cmd.getArgs();

    String tsaURL = args[0];
    File inputFile = new File(args[1]);
    File outputFile = new File(args[2]);

    File tempFile = new File(args[1] + ".tmp");

    addTimestamp(tsaURL, inputFile, tempFile, password);
    addLTV(tempFile, outputFile, password);

    tempFile.delete();
  }

  private static void addTimestamp(String tsaURL, File inputFile, File outputFile, String password) throws IOException {
    CreateSignedTimeStamp signing = new CreateSignedTimeStamp(tsaURL);
    signing.signDetached(inputFile, outputFile, password);
  }

  private static void addLTV(File inputFile, File outputFile, String password) throws IOException {
    Security.addProvider(SecurityProvider.getProvider());
    AddValidationInformation addOcspInformation = new AddValidationInformation();
    addOcspInformation.validateSignature(inputFile, outputFile, password);
  }
}
