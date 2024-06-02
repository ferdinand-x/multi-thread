package chapter1.chapter1.cancel;

import lombok.Data;

/**
 * @author : PARADISE
 * @ClassName : SleepMan
 * @description : a man who want to sleepp
 * @since : 2024/5/31 22:35
 */
@Data
public class SleepMan {

    private static final int NORMAL_SLEEP_TIMES = 10;

    private static final int LONG_SLEEP_TIMES = 100;

    private static final int LINE = 290;
    private int id;

    private int expectSleepTime;

    private int actualSleepTime;

    private boolean hasError;

    private String threadName;
    private String details;

    public static SleepMan create(int id) {
        var sleepMan = new SleepMan();
        sleepMan.setId(id);
        if (id <= LINE) {
            sleepMan.setExpectSleepTime(id * NORMAL_SLEEP_TIMES);
        } else {
            sleepMan.setExpectSleepTime(id * LONG_SLEEP_TIMES);
        }
        return sleepMan;
    }

}
