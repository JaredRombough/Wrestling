package wrestling.model.utility;

import java.io.File;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import wrestling.model.Worker;

public final class UtilityFunctions {

    //returns a random int between the two passed ints
    public static int randRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    //shows an image if it exists, handles hide/show of image frame
    public static void showImage(File imageFile, StackPane imageFrame, ImageView imageView) {

        if (imageFile.exists() && !imageFile.isDirectory()) {
            //show the border if it is not visible
            if (!imageFrame.visibleProperty().get()) {
                imageFrame.setVisible(true);
            }
            Image image = new Image("File:" + imageFile);
            imageView.setImage(image);
        } else //hide the border if it is visible
        {
            if (imageFrame.visibleProperty().get()) {
                imageFrame.setVisible(false);
            }
        }
    }

    public static String slashNames(List<Worker> workers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < workers.size(); i++) {
            sb.append(workers.get(i).getName());
            if (workers.size() - i > 1) {
                sb.append("\\");
            }
        }

        return sb.toString();
    }

    public static int weekOfMonth(LocalDate date) {
        Calendar ca1 = Calendar.getInstance();
        ca1.set(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
        return ca1.get(Calendar.WEEK_OF_MONTH);
    }

}
