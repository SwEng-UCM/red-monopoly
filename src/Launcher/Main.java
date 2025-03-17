package Launcher;
import Model.*;
import Controller.*;
import View.MainWindow;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws Exception {
        Controller controller = new Controller(MonopolyGame.getInstance());
        new MainWindow(controller);
    }
}
