package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: ZHY
 * Date: 15-10-16
 * Time: 下午11:05
 */
public class Test extends JPanel implements ActionListener {
    JTextField field1;
    JTextArea area1;
    JButton b1;
    int t = 0;

    public Test() {
        setLayout(new BorderLayout());
        b1 = new JButton("查询");
        field1 = new JTextField(10);
        area1 = new JTextArea(10, 20);
        JPanel p1 = new JPanel(), p2 = new JPanel();
        p1.add(new JLabel("输入查询的单词"));
        p1.add(field1);
        p1.add(b1);
        add(p1, "North");
        p2.add(area1);
        add(p2, "Center");
        b1.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        new Test();
    }
}
