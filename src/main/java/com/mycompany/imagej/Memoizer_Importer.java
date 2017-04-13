package com.mycompany.imagej;
/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import ij.IJ;
import ij.ImagePlus;
import ij.io.DirectoryChooser;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.io.IOException;

import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.Memoizer;
import loci.plugins.BF;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

public class Memoizer_Importer implements PlugIn {

  public void run(String arg) {
    // prompt user for directory to process
    OpenDialog od = new OpenDialog("Select LIF File to Process...", arg);
    String name = od.getFileName();
    String pathFile = od.getPath();
    DirectoryChooser dc = new DirectoryChooser("Select/Create Output Directory");
    String savePath = dc.getDirectory();
    try {
        ImageProcessorReader r = new ImageProcessorReader(
            new Memoizer(new ChannelSeparator(LociPrefs.makeImageReader())));
        IJ.showStatus("Examining file " + name);
        r.setId(pathFile);
        int num = r.getSeriesCount();
        r.close();

        for (int i=0; i<num; i++) {
          IJ.showStatus("Reading series #" + (i + 1) + "/" + num);
          String options = "open=["+pathFile+"] autoscale color_mode=Default view=Hyperstack stack_order=XYCZT series_"+i;
          ImagePlus[] imp = BF.openImagePlus(options);
          IJ.showStatus("Constructing image");
          imp[0].show();
          String saveName = String.format("%04d", i);
          IJ.saveAs("Tiff", savePath+saveName+".tif");
        }
        
        IJ.showStatus("Complete");
      }
      catch (FormatException exc) {
        IJ.error("Sorry, an error occurred: " + exc.getMessage());
      }
      catch (IOException exc) {
        IJ.error("Sorry, an error occurred: " + exc.getMessage());
      }
    IJ.showStatus("");
  }

}
