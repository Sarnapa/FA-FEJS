package DataService;

import java.io.File;
import java.util.ArrayList;

/**
 * To provide files list in directory that is given as parameter.
 * This class let user not to overwrite existing pdf file accidentally.
 */

public class FolderContentReader
{
    private String dirPathName;
    private ArrayList<String> fileNames = new ArrayList<>();

    public FolderContentReader(String dirPathName)
    {
        this.dirPathName = dirPathName;
        getFilesList();
    }

    private void getFilesList()
    {
        File directory = new File(dirPathName);
        File[] filesList = directory.listFiles();
        if(filesList != null) {
            for (File f : filesList)
                fileNames.add(f.getName());
        }
    }

    public boolean isFile(String name)
    {
        for(String fileName: fileNames)
        {
            if(fileName.equals(name))
                return true;
        }
        return false;
    }
}
