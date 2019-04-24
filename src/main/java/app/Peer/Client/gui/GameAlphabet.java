package app.Peer.Client.gui;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

public class GameAlphabet extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ALPHABET_ROW = 2;
    private static final int ALPHABET_COL = 13;

    private JButton[][] alphabet = new JButton[ALPHABET_ROW][ALPHABET_COL];

    public static class GameAlphabetHolder {
        private static final GameAlphabet INSTANCE = new GameAlphabet();
    }

    private GameAlphabet() {
        this.setSize(390, 60);
        setLayout(new GridLayout(ALPHABET_ROW, ALPHABET_COL));

        Character c = 'A';
        for (int i = 0; i < ALPHABET_ROW; i++) {
            for (int j = 0; j < ALPHABET_COL; j++) {
                alphabet[i][j] = new JButton(Character.toString(c));
                alphabet[i][j].setFont(new Font("Arial", Font.PLAIN, 15));
                add(alphabet[i][j]);
                alphabet[i][j].addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        JButton button = (JButton) e.getSource();
                        TransferHandler handle = button.getTransferHandler();
                        handle.exportAsDrag(button, e, TransferHandler.COPY);
                    }
                });
                alphabet[i][j].setTransferHandler(new SendTransferHandler(Character.toString(c)));
                c++;
            }
        }
    }

    public static final GameAlphabet get() {
        return GameAlphabetHolder.INSTANCE;
    }

    public class SendTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;
        private String str;

        public SendTransferHandler(String str) {
            this.str = str;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            Transferable t = new StringSelection(str);
            return t;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return DnDConstants.ACTION_COPY_OR_MOVE;
        }
    }
}
