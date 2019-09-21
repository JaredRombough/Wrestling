package openwrestling.file;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


public class ImportUtilsTest {

    @Test
    public void getFileString() {
        String fileName = "wrestler";
        File file = new File(".\\test_data");
        String fileString = ImportUtils.getFileString(file, fileName);
        assertThat(fileString).isNotNull();
    }

}