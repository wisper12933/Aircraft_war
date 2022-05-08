package thread;

import thread.stopFlag.BossAppearFlag;
import thread.stopFlag.GameStopFlag;

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;

public class BgmThread extends MusicThread{
    public BgmThread(String filename) {
        super(filename);
    }
    @Override
    public void readSoundData(int numBytesRead, InputStream source, byte[] buffer, SourceDataLine dataLine) throws IOException {
        while (numBytesRead != -1 && !(BossAppearFlag.bossAppearFlag) && !(GameStopFlag.gameOverFlag)) {
            //从音频流读取指定的最大数量的数据字节，并将其放入缓冲区中
            numBytesRead =
                    source.read(buffer, 0, buffer.length);
            //通过此源数据行将数据写入混频器
            if (numBytesRead != -1 ) {
                dataLine.write(buffer, 0, numBytesRead);
            }
        }
    }
    @Override
    public void run(){
        super.run();
    }
}
