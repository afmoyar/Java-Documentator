//Code taken from:
//https://mkyong.com/java/how-to-delete-directory-in-java/#:~:text=To%20delete%20a%20directory%2C%20you,in%20order%20to%20delete%20it.
import java.io.File;
import java.io.IOException;

public class FolderDeletion
{
    public static void deleteFolder(String SRC_FOLDER)
    {

        File directory = new File(SRC_FOLDER);

        //make sure directory exists
        if(!directory.exists()){



        }else{

            try{

                delete(directory);

            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    public static void delete(File file)
            throws IOException{

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();


            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }

        }else{
            //if file, then delete it
            file.delete();

        }
    }
}