package openwrestling.file;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImportUtils {
    static int hexStringToInt(String hexValueString) {
        return Integer.parseInt(hexValueString, 16);
    }

    static String hexStringToLetter(String hexValueString) {
        //take the characters in two positions, since they combine to make
        //up one hex value that we have to translate
        String letter = "";
        //translate the hex value string to an int value
        int intLetter = hexStringToInt(hexValueString);

        //only keep numbers that translate to an ascii alphabet value
        //otherwise just put a blank in our string
        //this will need to be more complex if we import more than just names
        if (intLetter >= 0 && intLetter <= 499) {
            letter += String.valueOf((char) (intLetter));
        } else {
            letter += " ";
        }

        return letter;
    }

    static String getFileString(File importFolder, String fileName) {
        Path path = Paths.get(importFolder.getPath() + "\\" + fileName + ".dat");
        byte[] data;

        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return DatatypeConverter.printHexBinary(data);
    }

    static List<List<String>> getHexLines(File importFolder, String fileName, int lineSize) {
        String fileString = getFileString(importFolder, fileName);
        List<List<String>> hexLines = new ArrayList<>();
        List<String> hexLine = new ArrayList<>();
        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {
            String hexValueString = String.valueOf(fileString.charAt(i)) + fileString.charAt(i + 1);
            hexLine.add(hexValueString);
            counter++;
            if (counter == lineSize) {
                hexLines.add(hexLine);
                hexLine = new ArrayList<>();
                counter = 0;
            }
        }
        return hexLines;
    }

    static String hexLineToTextString(List<String> hexLine) {
        List<String> toLetters = hexLine.stream().map(ImportUtils::hexStringToLetter).collect(Collectors.toList());
        return StringUtils.join(toLetters, null);
    }

}
