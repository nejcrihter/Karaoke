module com.example.karaoke {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires TarsosDSP.core;
    requires TarsosDSP.jvm;
    requires java.desktop;
    requires com.goxr3plus.streamplayer;
    //    requires com.goxr3plus.streamplayer;

    opens com.example.karaoke to javafx.fxml;
    exports com.example.karaoke;
}