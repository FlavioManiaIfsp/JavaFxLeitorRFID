package javafxarduino;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mania
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button btnConectar;

    @FXML
    private ComboBox cmbPortas;

    @FXML
    private Label label;

    private SerialPort porta;   

    private int statusLed = 0;

    @FXML
    private Button btnLigaDesliga;
    @FXML
    private Circle objImagem;
    @FXML
    private Button btnSerial;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        objImagem.setFill(Color.TRANSPARENT);
        carregarPortas();
        btnLigaDesliga.setDisable(true);
    }

    private void carregarPortas() {
        SerialPort[] portaNomes = SerialPort.getCommPorts();
        for (SerialPort portaNome : portaNomes) {
            cmbPortas.getItems().add(portaNome.getSystemPortName());
        }
    }

    @FXML
    private void conectar(ActionEvent event) {
        if (btnConectar.getText().equals("Conectar")) {
            porta = SerialPort.getCommPort(cmbPortas.getSelectionModel().
                    getSelectedItem().toString());
            if (porta.openPort()) {
                btnConectar.setText("Desconectar");
                cmbPortas.setDisable(true);
                btnLigaDesliga.setDisable(false);
            }

        } else {
            porta.closePort();
            cmbPortas.setDisable(false);
            btnLigaDesliga.setDisable(true);
            btnConectar.setText("Conectar");
        }
    }

    @FXML
    private void ligarLed(ActionEvent event) throws IOException {

        PrintWriter saida = new PrintWriter(porta.getOutputStream());
        if (statusLed == 0) {
            saida.print("1");
            statusLed = 1;
        } else {
            saida.print("0");
            statusLed = 0;
        }
        saida.flush();

        if (porta.isOpen()) {
            porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 300, 0);
            BufferedReader in = new BufferedReader(new InputStreamReader(porta.getInputStream()));
            try {
                String msg = in.readLine();
                System.out.println(msg);
                if (msg.equals("Desligado")) {
                    objImagem.setFill(Color.rgb(235, 87, 87));                                        
                } else {
                    objImagem.setFill(Color.rgb(203, 180, 212));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ler() {        
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
                while (true) {
                    lerdados();                    
                }
            }
        };
        t.start();
    }

    public void lerdados() {
        if (porta.isOpen()) {
            porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
            BufferedReader in = new BufferedReader(new InputStreamReader(porta.getInputStream()));
            try {
                String msg = in.readLine();
                if (msg.equals("73:69:5E:02")){
                    objImagem.setFill(Color.WHITE);
                    System.out.println("Cartão: "+msg);
                }else if(msg.equals("A4:9F:24:FC")){
                    objImagem.setFill(Color.BLUE);
                    System.out.println("Cahveiro: "+msg);                         
                }else{
                    objImagem.setFill(Color.TRANSPARENT);
                    System.out.println("Nenhuma Leitura: 00:00:00:00" + msg);
                }  
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
