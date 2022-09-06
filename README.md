[![GitHub Stars](https://img.shields.io/github/stars/RUB-Bioinf/CellomicsArrayScanPuzzleHelper.svg?style=social&label=Star)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper)
&nbsp;
[![GitHub Downloads](https://img.shields.io/github/downloads/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/total?style=social)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/releases)
&nbsp;
[![Follow us on Twitter](https://img.shields.io/twitter/follow/NilsFoer?style=social&logo=twitter)](https://twitter.com/intent/follow?screen_name=NilsFoer)

***

[![Contributors](https://img.shields.io/github/contributors/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/graphs/contributors)
&nbsp;
[![License](https://img.shields.io/github/license/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?color=green&style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/LICENSE)
&nbsp;
![Size](https://img.shields.io/github/repo-size/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)
&nbsp;
[![Issues](https://img.shields.io/github/issues/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/issues)
&nbsp;
[![Pull Requests](https://img.shields.io/github/issues-pr/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/pulls)
&nbsp;
[![Commits](https://img.shields.io/github/commit-activity/m/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/)
&nbsp;
[![Latest Release](https://img.shields.io/github/v/release/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/)
&nbsp;
[![Release Date](https://img.shields.io/github/release-date/RUB-Bioinf/CellomicsArrayScanPuzzleHelper?style=flat)](https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper/releases)

# Technical Setup

This project has been compiled using *Java 11*.
Cloning and editing this repository will work out of the box, no additional packages or lirbraries are required.

# How to use
## Integration into OmniSphero

This tool is designed to be used in conjunction with the OmniSphero Software.
The software was originally published at [1] by Schmuck et al.
Since then the software was maintained, updated with new features, and used by Nils Förster and Hagen Eike Keßel.

During the *puzzling* step when using OmniSphero, the software asks for a *Smart Well*.
You can use the software in this repository to create such a smart well.
Safe the newly created smart well on your device and load it into OmniSphero when prompted to.

Learn more about the software here: www.omnisphero.com

## How to use out of the box

To learn how to use this tool, run it and take a look at the UI.
Refer to the headers below to learn more about the corresponding headers in the UI.

### Plating Partitions
If you want to create more than one OmniSphero save-state from a single well, you can create individual *well partitions*.
You can adjust the number of partitions using the corresponding Spinner.

For every partition you must declare the well distributions.
Use the mouse and drag & drop to toggle wells to be marked as evaluated by OmniSphero (red), control (blue) or ignored (white).
**Every partition must have at least one control**.

Flip through declared partitions using the corresponding tabs.
The numbers after the names sum the wells to be evaluated and controls per partition.

You can override the character to indicate different partitions.
This usually should not be edited.
Make sure the same character is implemented in OmniSphero.

### Well Preview
This is a preview window for the number of tiles, and the spiral pattern to be puzzled (optionally highlighted).
To influence this preview, see below.

Use the +/- buttons to adjust font size.

### Smart Well Properties
Use this section of the UI to set up how the well tiles will be puzzled.
This should replicate the puzzling process of your tiling / microscope.

#### Spiral Properties
Use the "puzzle width" and "puzzle height" properties to describe how many tiles will be in the spiral. 
Declare the size of every tile (in px) using the corresponding spinners.
If you don't know your image's dimensions, load a sample using the corresponding button below.

You can declare the spiral's orientation (down or right) using the corresponding drop down menu.
Use the preview (see above) for a preview how the tiles in the spiral pattern will be aligned.

#### Magnification
Declare your um-to-pixel ratio here.
Dimensions in OmniSphero are not calculated in pixels, so declare the correct ratio here for accurate measurements.

### Tile Image Flipping
The checkboxes in this section to declare if you want to flip images.

If checked, "Flip individual images along the _x_ axis", will cause every individual tile image to be flipped.
Their position is unchanged.

If checked, "Flip final image along the _x_ axis", will mirror the final puzzled image.

### Tile Positioning
The checkboxes in this section to declare if you want to mirror the tile positions.

If checked, "Mirror horizontal tile position", will flip the position of the tiles on the spiral horizontally.

If checked, "Mirror vertical tile position", will flip the position of the tiles on the spiral vertically.

### Actions
Use these buttons to control the UI and data.

 - "Refresh Preview": If you have disabled auto refreshing in the menu bar, you can refresh the previews manually using this button.
 - "Import Instructions": Choose a previously created Smart Well file and import its properties (see below).
 - "Export Instruction File": Saves the currently declared settings to a file on your device. Use this file in conjunction with OmniSphero (see above).

# References
1. Schmuck, M.R., Temme, T., Dach, K. et al. Omnisphero: a high-content image analysis (HCA) approach for phenotypic developmental neurotoxicity (DNT) screenings of organoid neurosphere cultures in vitro. Arch Toxicol 91, 2017–2028 (2017). https://doi.org/10.1007/s00204-016-1852-2

****

**Funding**: This research received no external funding.

**Conflicts of Interest**: The authors declare no conflict of interest.
