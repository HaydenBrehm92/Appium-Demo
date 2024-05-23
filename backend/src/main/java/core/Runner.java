package core;
import UI.MainForm;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import org.java_websocket.WebSocket;

import javax.swing.*;
import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Main entry point for the program.
 */
@SpringBootApplication
@EnableScheduling
public class Runner {

    /**
     * Creates the GUI to be interfaced with.
     */
    private static void createAndShowGui() {
        FlatLaf.registerCustomDefaultsSource( "com.kodiakptt.themes" );
        FlatDarculaLaf.setup();
        MainForm mainForm = new MainForm();
        JFrame frame = new JFrame("EEI Automation Utility");
        frame.setContentPane(mainForm.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setJMenuBar(mainForm.getMenuBar());
        frame.setVisible(true);
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE) public static void doNotShutdown() {}
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
        doNotShutdown();
        // WEBSOCKET CONNECTION
        /*try{
            SocketServer socket = new SocketServer(8080);
            socket.setReuseAddr(true);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.stop(5000, "[Server] Shutting Down");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
            socket.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


        //Intentional deadlock
        try {
            Thread.currentThread().join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/

        /*
         * Proper way to use Swing.
         * Explanation here: https://stackoverflow.com/questions/6567870/what-does-swingutilities-invokelater-do
         * "As other answers have said, it executes your Runnable on the AWT event-dispatching thread.
         * But why would you want to do that? Because the Swing data structures aren't thread-safe,
         * so to provide programmers with an easily-achievable way of preventing concurrent access to them,
         * the Swing designers laid down the rule that all code that accesses them must run on the same thread.
         * That happens automatically for event-handling and display maintenance code,
         * but if you've initiated a long-running action - on a new thread, of course - how can you signal its progress or completion?
         * You have to modify a Swing control, and you have to do it from the event-dispatching thread. Hence, invokeLater."
         */
        /*SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });*/
    }
}

