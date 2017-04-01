package DataService;

import java.io.File;
import java.util.ArrayList;

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
