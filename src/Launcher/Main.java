package Launcher;
import Model.*;
import Controller.*;
import View.MainWindow;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {

    private static void play_console_mode(){

    }

    private static void play_GUI_mode(){

    }

    public static void main(String[] args) throws Exception {
        Controller controller =
                new Controller(new MonopolyGame());
        new MainWindow(controller);
        //SwingUtilities.invokeAndWait(()-> new MainWindow(controller));


        //trying out changes
    }
}