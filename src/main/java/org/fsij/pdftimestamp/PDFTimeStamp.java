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

import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import org.apache.log4j.BasicConfigurator;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/*
 * Usage:
 *   pdftimestamp [-p PDF-PASSWORD] TSA-URL INPUT-PDF OUTPUT-PDF
 */

public class PDFTimeStamp {
  public static void main(String[] args)
  throws ParseException {

    BasicConfigurator.configure();

    Options options = new Options();

    options.addOption("p", true, "PDF password");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String password = cmd.getOptionValue("p");

    args = cmd.getArgs();

    String tsaURL = args[0];
    File inputFile = new File(args[1]);
    File outputFile = new File(args[2]);

    File tempFile = new File(args[1] + ".tmp");

    addTimestamp(tsaURL, inputFile, tempFile, password);
    addLTV(tempFile, outputFile, password);

    tempFile.delete();
  }

  private static void addTimestamp(String tsaURL, File inputFile, File outputFile, String password) {
    CreateSignedTimeStamp signing = new CreateSignedTimeStamp(tsaURL);
    try { signing.signDetached(inputFile, outputFile, password); }
    catch (IOException e) { System.out.println("signing.signDetached"); System.exit(1); return; }
  }

  private static void addLTV(File inputFile, File outputFile, String password) {
    try { Security.addProvider(SecurityProvider.getProvider()); }
    catch (IOException e) { System.out.println("Security.addProvider"); System.exit(1); return; }
    AddValidationInformation addOcspInformation = new AddValidationInformation();
    try { addOcspInformation.validateSignature(inputFile, outputFile, password); }
    catch (IOException e) { System.out.println("addOcspInformation.validateSignature"); System.exit(1); return; }

  }
}
