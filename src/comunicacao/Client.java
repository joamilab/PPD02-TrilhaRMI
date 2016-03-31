/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import model.Player;
import negocio.Jogadas;
import negocio.Tabuleiro;
import telatrilha.JogoTrilha;

/** EXECUTE THIS AFTER SERVER
 *
 * @author Joamila
 * Created: 11/03/2016
 * Last Modified: 22/03/2016
 */
public class Client extends Thread{
    //Interface gráfica
    private JogoTrilha tabuleiro;
    private JTextField txtNome;
    
    //Classes auxiliares
    public Player player = new Player();
    Tabuleiro tabuleiroMetod = new Tabuleiro();
    Jogadas jogadas = new Jogadas();
    
    //Listeners
    ActionListener listenerActionPerf;
    KeyListener listenerKey;
    MouseListener listenerMouse;
    WindowListener listenerWind;
    
    //Variáveis auxiliares
    String jogadaIni = "";
    String jogadaFin = "";
    String iniUltimaJog = "";
    String finUltimaJog = "";
    String pecaExcluida = "";
    int pecasAdversario = 9;
    int qtdJogadasRestantes = 10;
    
    public Client() throws RemoteException{
        
        JLabel lblMessage = new JLabel("Digite seu nome");
        txtNome = new JTextField("User");
        Object[] texts = {lblMessage, txtNome};
        JOptionPane.showMessageDialog(null, texts);

        player.setNome(txtNome.getText());
        player.setCor("");
        
        this.tabuleiro = new JogoTrilha();
        this.tabuleiro.setVisible(true);
    }       
     
    public static void main(String args[]){
        try {
            final TrilhaInterface interfaceServer = (TrilhaInterface) Naming.lookup("//localhost:12345/ServerTrilha");
            System.out.println("Servidor localizado!");
            
            final Client cliente = new Client();
            
            Thread thread;
            thread = new Thread(new Runnable(){
                @Override
                public void run() {    
                    try {
                        //Conexão do cliente
                        cliente.player.setId(interfaceServer.conectarCliente(cliente.player.getNome()));
                        
                        //Inicializar tabuleiro
                        cliente.iniciarCasas(cliente.tabuleiro, cliente.listenerMouse);
                        cliente.iniciarListenerComponents(cliente.tabuleiro, cliente.listenerActionPerf, 
                                cliente.listenerKey, cliente.listenerWind);     
                        cliente.tabuleiroMetod.iniciarTabuleiro();            
                        cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                        cliente.tabuleiro.getIniciarJogoButton().setEnabled(false);
                        
                        while(cliente.player.getFlagAtiva()){
                            try{
                                //Fechamento da janela
                                if(interfaceServer.checkFecharJanela()){
                                    cliente.tabuleiro.getStatusJogoLabel().setText("Seu adversário saiu da sala.\r\n");
                                    cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                    cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                                    cliente.tabuleiro.getIniciarJogoButton().setEnabled(false);
                                }
                                
                                //Conversa chat
                                if(interfaceServer.checkMensagem(cliente.player.getId())){
                                String mensagem = interfaceServer.receberMensagemChat();
                                    cliente.tabuleiro.conversaTextArea.append(mensagem + "\r\n");
                                }
                                
                                //Adversário disponível
                                if(interfaceServer.checkAdversarios() && cliente.player.getFlagAdversario()){
                                    cliente.tabuleiro.getIniciarJogoButton().setEnabled(true);
                                    cliente.tabuleiro.getStatusJogoLabel().setText("Pronto para o desafio? Você já tem um adversário.");
                                    cliente.player.setFlagAdversario(false);
                                }
                                //Definir cor das peças
                                else if(interfaceServer.getCorPecas(cliente.player.getId()).equals("vermelha") && cliente.player.getFlagCor()==0){
                                    cliente.player.setCor("vermelha");
                                    cliente.player.setFlagCor(1);

                                    cliente.tabuleiroMetod.iniciarPecas(cliente.tabuleiro, cliente.player.getCor());
                                    cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                    
                                    cliente.tabuleiro.getIniciarJogoButton().setEnabled(false);
                                    cliente.tabuleiro.getStatusJogoLabel().setText("Seu adversário iniciará o jogo.");
                                    cliente.tabuleiro.getStatusJogoLabel().setForeground(Color.red);
                                }
                                //Identificar jogadas
                                else if(interfaceServer.checkUltimaJogada(cliente.player.getId())){
                                    ArrayList<String> movimento = interfaceServer.receberJogada();
                                    cliente.iniUltimaJog = movimento.get(0);
                                    cliente.finUltimaJog = movimento.get(1);
                                    
                                    if(cliente.player.getCor().equals("azul")){
                                        cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, "vermelha", cliente.iniUltimaJog, 
                                                cliente.finUltimaJog, 200);
                                    } else{
                                       cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, "azul", cliente.iniUltimaJog, 
                                                cliente.finUltimaJog, 200); 
                                    }
                                    
                                    //Moinho
                                    if(interfaceServer.checkMoinho(cliente.player.getId())){
                                        cliente.tabuleiro.getStatusJogoLabel().setText("Seu adversário fez um moinho. Aguarde "
                                            + "enquanto uma peça é excluida!");
                                    }
                                    //Empate
                                    else if(interfaceServer.checkEmpate(cliente.player.getId())){
                                        cliente.jogadas.informarEmpate(cliente.tabuleiro);
                                        cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                    }
                                    //Trancamento
                                    else if(cliente.tabuleiroMetod.getQtdPecas()>3 && cliente.tabuleiroMetod.getQtdPecasJogadas()>=9){
                                        //O jogador identificou um trancamento.
                                        if(cliente.jogadas.identificarTrancamento(cliente.tabuleiroMetod.getCasas(), 
                                                cliente.player.getCor())){
                                            cliente.jogadas.identificarFimJogo(cliente.tabuleiro, 100);
                                            interfaceServer.informarVitoria(cliente.player.getId());
                                        }
                                        //Não houve trancamento.
                                        else{
                                            cliente.jogadas.retomarVez(cliente.tabuleiro);
                                            cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                        }
                                    }
                                    else{
                                        cliente.jogadas.retomarVez(cliente.tabuleiro);
                                        cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                    }
                                }
                                //Peça excluída
                                else if(interfaceServer.checkPecaExcluida(cliente.player.getId())){
                                    String peca = interfaceServer.receberExclusao();
                                    cliente.pecaExcluida = peca;
                                    cliente.player.setFlagExclusao(true);
                                    
                                    cliente.tabuleiroMetod.retirarPeca(cliente.tabuleiro, peca, 200);
                                    cliente.tabuleiro.getStatusJogoLabel().setText("Que pena, você perdeu uma peça!");
                                    cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                    cliente.jogadas.retomarVez(cliente.tabuleiro);
                                    cliente.tabuleiroMetod.setQtdPecas(cliente.tabuleiroMetod.getQtdPecas()-1);
                                    
                                    //O jogador tem apenas duas peças.
                                    if(cliente.tabuleiroMetod.getQtdPecas() <= 2){
                                        cliente.jogadas.identificarFimJogo(cliente.tabuleiro, 100);
                                        cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                        
                                        interfaceServer.informarVitoria(cliente.player.getId());
                                    }
                                    //Trancamento
                                    else if(cliente.tabuleiroMetod.getQtdPecas()>3 && cliente.tabuleiroMetod.getQtdPecasJogadas()>=9){
                                        //O jogador identificou um trancamento.
                                        if(cliente.jogadas.identificarTrancamento(cliente.tabuleiroMetod.getCasas(), 
                                                cliente.player.getCor())){
                                            cliente.jogadas.identificarFimJogo(cliente.tabuleiro, 100);
                                            interfaceServer.informarVitoria(cliente.player.getId());
                                        }
                                    }
                                }
                                //Vitória
                                else if(interfaceServer.checkVitoriaAdv(cliente.player.getId())){
                                    cliente.jogadas.identificarFimJogo(cliente.tabuleiro, 200);
                                    cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                }
                                //Voltar jogo
                                else if(interfaceServer.checkVoltarJogo(cliente.player.getId())){
                                    cliente.jogadas.retomarVez(cliente.tabuleiro);
                                    cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, cliente.player.getCor(), 
                                            cliente.jogadaFin, cliente.jogadaIni, 100);
                                    cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                    cliente.tabuleiroMetod.habilitaBotoes(cliente.tabuleiro);
                                    
                                    //Voltar jogo após um moinho
                                    if(cliente.player.getFlagExclusao()){
                                        if(cliente.player.getCor().equals("azul")){
                                            cliente.tabuleiroMetod.adicionarPeca(cliente.tabuleiro, cliente.pecaExcluida, "vermelha");
                                            cliente.player.setFlagExclusao(false);
                                        }
                                        else{
                                            cliente.tabuleiroMetod.adicionarPeca(cliente.tabuleiro, cliente.pecaExcluida, "azul");
                                            cliente.player.setFlagExclusao(false);
                                        }
                                    }
                                }
                                //Solicitação do reinício de jogo
                                else if(interfaceServer.checkSolicReinicio(cliente.player.getId())){
                                    int resposta;

                                    cliente.tabuleiro.getStatusJogoLabel().setText("O seu adversário deseja reiniciar o jogo.");
                                    resposta = JOptionPane.showConfirmDialog(null, "Você aceita reiniciar o jogo?");

                                    //O jogador concordou com o reinício do jogo.
                                    if(resposta == JOptionPane.OK_OPTION){
                                        cliente.tabuleiroMetod.reiniciarTabuleiro(cliente.tabuleiro);
                                        cliente.tabuleiroMetod.reiniciarPecas(cliente.tabuleiro, cliente.player.getCor());
                                        
                                        if(cliente.player.getCor().equals("azul")){
                                            cliente.tabuleiro.getStatusJogoLabel().setText("Você iniciará o jogo. Faça sua jogada!");
                                            cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                            cliente.tabuleiroMetod.habilitaBotoes(cliente.tabuleiro);
                                        }    
                                        else{
                                            cliente.tabuleiro.getStatusJogoLabel().setText("Seu adversário iniciará o jogo.");
                                            cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                            cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                                        }       
                                    }
                                    //O jogador não concordou com o reinício do jogo.
                                    else{
                                        cliente.tabuleiro.getStatusJogoLabel().setText("O jogo não será reiniciado. Aguarde a sua vez!");
                                        cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                        cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                                    }

                                    interfaceServer.responderSolicitacaoReinicio(cliente.player.getId(), resposta);
                                }
                                //Resposta da solicitação de reinício
                                else if(interfaceServer.checkRespostaReinicio(cliente.player.getId())){
                                    int res = interfaceServer.receberRespostaReinicio();
                                   
                                    if(res == JOptionPane.OK_OPTION){
                                        cliente.tabuleiroMetod.reiniciarTabuleiro(cliente.tabuleiro);
                                        cliente.tabuleiroMetod.reiniciarPecas(cliente.tabuleiro, cliente.player.getCor());
                                        if(cliente.player.getCor().equals("azul")){
                                            cliente.tabuleiro.getStatusJogoLabel().setText("Você iniciará o jogo. Faça sua jogada!");
                                            cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                            cliente.tabuleiroMetod.habilitaBotoes(cliente.tabuleiro);
                                        }   
                                        else{
                                            cliente.tabuleiro.getStatusJogoLabel().setText("Seu adversário iniciará o jogo."); 
                                            cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                            cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                                        }     
                                    }
                                    //O adversário não concordou com o reinício do jogo.
                                    else{
                                        cliente.tabuleiro.getStatusJogoLabel().setText("O jogo não será reiniciado. Faça a sua jogada!");
                                        cliente.habilitarListenersLabel(cliente.tabuleiro, cliente.listenerMouse);
                                        cliente.tabuleiroMetod.habilitaBotoes(cliente.tabuleiro);
                                    }
                                }
                                //Desistência
                                else if(interfaceServer.checkDesistencia(cliente.player.getId())){
                                    cliente.tabuleiro.getStatusJogoLabel().setText("O seu adversário desistiu. Parabéns, você venceu!");
                                    cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                }
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        } catch (RemoteException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
            });
            
            cliente.listenerActionPerf = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    //Botão enviar: envia mensagem pelo chat.
                    if(arg0.getActionCommand().equals(cliente.tabuleiro.getEnviarButton().getActionCommand())){
                        if(!cliente.tabuleiro.getMensagemTextField().getText().equals("")){
                            cliente.tabuleiro.conversaTextArea.append("Eu: " + cliente.tabuleiro.getMensagemTextField().getText() + "\r\n");
                            try {
                                interfaceServer.enviarMensagemChat(cliente.player.getId(), cliente.tabuleiro.getMensagemTextField().getText());
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cliente.tabuleiro.getMensagemTextField().setText("");
                        }
                    }
                    //Botão iniciar: inicia o jogo no tabuleiro, define a cor das peças.
                    else if(arg0.getActionCommand().equals(cliente.tabuleiro.getIniciarJogoButton().getActionCommand())){
                        try {
                            cliente.player.setCor(interfaceServer.definirCor(cliente.player.getId()));
                
                            cliente.tabuleiroMetod.iniciarPecas(cliente.tabuleiro, cliente.player.getCor());
                            cliente.tabuleiroMetod.habilitaBotoes(cliente.tabuleiro);

                            cliente.tabuleiro.getIniciarJogoButton().setEnabled(false);
                            cliente.tabuleiro.getStatusJogoLabel().setText("Você iniciará o jogo. Faça sua jogada!");
                            cliente.tabuleiro.getStatusJogoLabel().setForeground(Color.BLUE);

                        } catch (RemoteException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //Botão voltar: volta a jogada no tabuleiro.
                    else if(arg0.getActionCommand().equals(cliente.tabuleiro.getVoltarJogoButton().getActionCommand())){
                        if(cliente.iniUltimaJog.equals("p1Label") || cliente.iniUltimaJog.equals("p2Label") || cliente.iniUltimaJog.equals("p3Label")
                                || cliente.iniUltimaJog.equals("p4Label") || cliente.iniUltimaJog.equals("p5Label") || cliente.iniUltimaJog.equals("p6Label")
                                || cliente.iniUltimaJog.equals("p7Label") || cliente.iniUltimaJog.equals("p8Label") || cliente.iniUltimaJog.equals("p9Label")){

                            cliente.tabuleiroMetod.setQtdPecasJogadas(cliente.tabuleiroMetod.getQtdPecasJogadas() - 1);
                            cliente.tabuleiroMetod.retirarPeca(cliente.tabuleiro, cliente.finUltimaJog, 200);
                        }
                        else{
                            if(cliente.player.getCor().equals("azul"))
                                cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, "vermelha", cliente.finUltimaJog, 
                                        cliente.iniUltimaJog, 200);
                            else
                                cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, "azul", cliente.finUltimaJog, 
                                        cliente.iniUltimaJog, 200);
                        }
                        
                        //Voltar jogo após um moinho
                        if(cliente.player.getFlagExclusao()){
                            cliente.tabuleiroMetod.adicionarPeca(cliente.tabuleiro, cliente.pecaExcluida, cliente.player.getCor());
                            cliente.player.setFlagExclusao(false);
                        }

                        cliente.jogadas.passarVez(cliente.tabuleiro);
                        cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                        cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                        try {
                            interfaceServer.voltarJogo(cliente.player.getId());
                        } catch (RemoteException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //Botão reiniciar: solicita o reinício do jogo.
                    else if(arg0.getActionCommand().equals(cliente.tabuleiro.getReiniciarJogoButton().getActionCommand())){
                        cliente.tabuleiro.getStatusJogoLabel().setText("A solicitação foi enviada. Aguarde a resposta!");
                        cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                        cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                        
                        try {
                            interfaceServer.solicitarReinicio(cliente.player.getId());
                        } catch (RemoteException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    //Botão desistir: desiste do jogo.
                    else if(arg0.getActionCommand().equals(cliente.tabuleiro.getDesistirJogoButton().getActionCommand())){
                        int resposta;
                        resposta = JOptionPane.showConfirmDialog(null, "Você tem certeza que quer desistir?");

                        if(resposta == JOptionPane.OK_OPTION){
                            cliente.tabuleiro.getStatusJogoLabel().setText("Que pena, você perdeu o jogo!");
                            cliente.tabuleiroMetod.desabilitaBotoes(cliente.tabuleiro);
                            cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                            try {
                                interfaceServer.desistirJogo(cliente.player.getId());
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }       
                        else{
                            cliente.tabuleiro.getStatusJogoLabel().setText("Que bom, que você não desistiu! Agora faça sua jogada!");
                        }          
                    }
                }
            };
            
            cliente.listenerKey = new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    //Tecla enter: envia mensagem pelo chat.
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        if(!cliente.tabuleiro.getMensagemTextField().getText().equals("")){
                            cliente.tabuleiro.conversaTextArea.append("Eu: " + cliente.tabuleiro.getMensagemTextField().getText() + "\r\n");
                            try {
                                interfaceServer.enviarMensagemChat(cliente.player.getId(), cliente.tabuleiro.getMensagemTextField().getText());
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cliente.tabuleiro.getMensagemTextField().setText("");
                        }
                    } 
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    
                }
            };
            
            cliente.listenerMouse = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //Seleção da peça a ser movida.
                   if(cliente.player.getFlagJogada() == 0 && cliente.player.getFlagTrilha() == 0){
                        cliente.jogadaIni = cliente.jogadas.verificarValidadePeca(cliente.tabuleiro, 
                                (JLabel) e.getComponent(), cliente.player.getCor(), cliente.tabuleiroMetod.getCasas(), 1);
                        
                        if(!cliente.jogadaIni.equals("")){
                            cliente.player.setFlagJogada(1);
                        }    
                    }
                    //Seleção da casa para onde a peça será movida.
                    else if (cliente.player.getFlagJogada() == 1 && cliente.player.getFlagTrilha() == 0){
                        cliente.jogadaFin = cliente.jogadas.verificarValidadePeca(cliente.tabuleiro, 
                                (JLabel) e.getComponent(), cliente.player.getCor(), cliente.tabuleiroMetod.getCasas(), 2);
                        if(!cliente.jogadaFin.equals("")){
                            cliente.player.setFlagJogada(0);

                            //O jogador e o seu adversário têm apenas 3 peças cada.
                            if(cliente.tabuleiroMetod.getQtdPecas() == 3 && cliente.pecasAdversario == 3)
                               cliente.qtdJogadasRestantes -= 1;

                            cliente.tabuleiroMetod.atualizarTabuleiro(cliente.tabuleiro, cliente.player.getCor(), 
                                   cliente.jogadaIni, cliente.jogadaFin, 100);
                            cliente.tabuleiroMetod.setQtdPecasJogadas(cliente.tabuleiroMetod.getQtdPecasJogadas() + 1);
                            
                            try {
                                interfaceServer.enviarJogada(cliente.player.getId(), cliente.jogadaIni, cliente.jogadaFin);
                                cliente.player.setFlagExclusao(false);
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //O jogador e o seu adversário tem 3 peças cada e já aconteceram 10 jogadas.
                            if(cliente.qtdJogadasRestantes == 0){
                                cliente.jogadas.informarEmpate(cliente.tabuleiro);
                                cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                try {
                                    interfaceServer.informarEmpate(cliente.player.getId());
                                } catch (RemoteException ex) {
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            //O jogador e o seu adversário tem 3 peças cada, mas ainda não aconteceram 10 jogadas.
                            else{
                                //O jogador formou um moinho.
                                if(cliente.jogadas.checarVizinhos(cliente.jogadaFin, cliente.tabuleiroMetod.getCasas())){
                                    try {
                                        cliente.tabuleiro.getStatusJogoLabel().setText("MOINHO! Você pode excluir uma peça adversária.");
                                        cliente.player.setFlagTrilha(1);
                                        interfaceServer.informarMoinho(cliente.player.getId());
                                    } catch (RemoteException ex) {
                                       Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                //O jogador não formou um moinho.
                                else{
                                    cliente.player.setFlagJogada(0);
                                    cliente.jogadas.passarVez(cliente.tabuleiro);
                                    cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                                }
                            }
                        }   
                    }   
                    else if (cliente.player.getFlagTrilha() == 1){
                        cliente.pecaExcluida = cliente.jogadas.verificarValidadePeca(cliente.tabuleiro, (JLabel) e.getComponent(), 
                                cliente.player.getCor(), cliente.tabuleiroMetod.getCasas(), 3);
                        
                        if(!cliente.pecaExcluida.equals("")){
                            cliente.player.setFlagTrilha(0);
                            cliente.player.setFlagExclusao(true);
                            
                            cliente.tabuleiroMetod.retirarPeca(cliente.tabuleiro, cliente.pecaExcluida, 100);
                            cliente.jogadas.passarVez(cliente.tabuleiro);
                            cliente.desabilitarListeners(cliente.tabuleiro, cliente.listenerMouse);
                            try {
                                interfaceServer.excluirPeca(cliente.player.getId(), cliente.pecaExcluida);
                                cliente.player.setFlagExclusao(true);
                                cliente.pecasAdversario -= 1;
                            } catch (RemoteException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    
                }
            };
            
            cliente.listenerWind = new WindowListener (){
                @Override
                public void windowOpened(WindowEvent e) {
                    
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        cliente.player.setFlagAtiva(false);
                        interfaceServer.fecharJanela();
                        System.exit(0);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    
                }

                @Override
                public void windowIconified(WindowEvent e) {
                    
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                    
                }

                @Override
                public void windowActivated(WindowEvent e) {
                    
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                    
                }  
            };
        
            thread.start();
        }catch(NotBoundException | MalformedURLException | RemoteException e){
            e.printStackTrace();
        } 
    }

    /**
     * Habilita os listeners dos botões, campo de texto onde a conversa do chat 
     * é digitada e da janela.
     * @param tabuleiro JogoTrilha
     * @param listenerA ActionListener
     * @param listenerK KeyListener
     * @param listenerW WindowListener
     */
    public void iniciarListenerComponents(JogoTrilha tabuleiro, ActionListener listenerA, KeyListener listenerK,
            WindowListener listenerW){
        java.util.List<JButton> botoes = Arrays.asList(tabuleiro.getEnviarButton(), tabuleiro.getIniciarJogoButton(),
                tabuleiro.getReiniciarJogoButton(), tabuleiro.getVoltarJogoButton(), tabuleiro.getDesistirJogoButton());
        
        tabuleiro.getMensagemTextField().addKeyListener(listenerK);
        tabuleiro.addWindowListener(listenerW);
        
        for (JButton botao : botoes) {
            botao.addActionListener(listenerA);
            botao.addKeyListener(listenerK);
        }
    }
    
    /**
     * Inicia as casas, atribuindo um nome e habilitando o listener de cada label
     * correspondentes às casas.
     * @param tabuleiro JogoTrilha
     * @param listenerM MouseListener
     */
    void iniciarCasas(JogoTrilha tabuleiro, MouseListener listenerM){
        java.util.List<JLabel> casas = Arrays.asList(tabuleiro.getA1Label(), tabuleiro.getA2Label(), tabuleiro.getA3Label(), 
                tabuleiro.getB1Label(), tabuleiro.getB2Label(), tabuleiro.getB3Label(),
                tabuleiro.getC1Label(), tabuleiro.getC2Label(), tabuleiro.getC3Label(),
                tabuleiro.getD1Label(), tabuleiro.getD2Label(), tabuleiro.getD3Label(),
                tabuleiro.getD4Label(), tabuleiro.getD5Label(), tabuleiro.getD6Label(),
                tabuleiro.getE1Label(), tabuleiro.getE2Label(), tabuleiro.getE3Label(),
                tabuleiro.getF1Label(), tabuleiro.getF2Label(), tabuleiro.getF3Label(),
                tabuleiro.getG1Label(), tabuleiro.getG2Label(), tabuleiro.getG3Label(),
                tabuleiro.getP1Label(), tabuleiro.getP2Label(), tabuleiro.getP3Label(),
                tabuleiro.getP4Label(), tabuleiro.getP5Label(), tabuleiro.getP6Label(),
                tabuleiro.getP7Label(), tabuleiro.getP8Label(), tabuleiro.getP9Label());
        
        java.util.List<String> nomeLabel =  Arrays.asList("a1Label", "a2Label", "a3Label", "b1Label", "b2Label", "b3Label",
                "c1Label", "c2Label", "c3Label", "d1Label", "d2Label", "d3Label", "d4Label", "d5Label", "d6Label",
                "e1Label", "e2Label", "e3Label", "f1Label", "f2Label", "f3Label", "g1Label", "g2Label", "g3Label",
                "p1Label", "p2Label", "p3Label", "p4Label", "p5Label", "p6Label", "p7Label", "p8Label", "p9Label");
        
        for(int i = 0; i < casas.size(); i++){
            casas.get(i).addMouseListener(listenerM);
            casas.get(i).setName(nomeLabel.get(i));
        }
    }
    
    /**
     * Habilita os listeners das casas, permitindo que elas sejam clicadas.
     * 
     * @param tabuleiro JogoTrilha
     * @param listenerM MouseListener
     */
    void habilitarListenersLabel(JogoTrilha tabuleiro, MouseListener listenerM){
        java.util.List<JLabel> pecas = Arrays.asList(tabuleiro.getA1Label(), tabuleiro.getA2Label(), tabuleiro.getA3Label(), 
                tabuleiro.getB1Label(), tabuleiro.getB2Label(), tabuleiro.getB3Label(),
                tabuleiro.getC1Label(), tabuleiro.getC2Label(), tabuleiro.getC3Label(),
                tabuleiro.getD1Label(), tabuleiro.getD2Label(), tabuleiro.getD3Label(),
                tabuleiro.getD4Label(), tabuleiro.getD5Label(), tabuleiro.getD6Label(),
                tabuleiro.getE1Label(), tabuleiro.getE2Label(), tabuleiro.getE3Label(),
                tabuleiro.getF1Label(), tabuleiro.getF2Label(), tabuleiro.getF3Label(),
                tabuleiro.getG1Label(), tabuleiro.getG2Label(), tabuleiro.getG3Label(),
                tabuleiro.getP1Label(), tabuleiro.getP2Label(), tabuleiro.getP3Label(),
                tabuleiro.getP4Label(), tabuleiro.getP5Label(), tabuleiro.getP6Label(),
                tabuleiro.getP7Label(), tabuleiro.getP8Label(), tabuleiro.getP9Label());
         
         for(JLabel casa : pecas){
             casa.addMouseListener(listenerM);
         }
    }
    
    /**
     * Desabilita os listeners das casas, impedindo assim que elas sejam clicadas.
     * 
     * @param tabuleiro JogoTrilha
     * @param listenerM MouseListener
     */
    void desabilitarListeners(JogoTrilha tabuleiro, MouseListener listenerM){
        java.util.List<JLabel> pecas = Arrays.asList(tabuleiro.getA1Label(), tabuleiro.getA2Label(), tabuleiro.getA3Label(), 
                tabuleiro.getB1Label(), tabuleiro.getB2Label(), tabuleiro.getB3Label(),
                tabuleiro.getC1Label(), tabuleiro.getC2Label(), tabuleiro.getC3Label(),
                tabuleiro.getD1Label(), tabuleiro.getD2Label(), tabuleiro.getD3Label(),
                tabuleiro.getD4Label(), tabuleiro.getD5Label(), tabuleiro.getD6Label(),
                tabuleiro.getE1Label(), tabuleiro.getE2Label(), tabuleiro.getE3Label(),
                tabuleiro.getF1Label(), tabuleiro.getF2Label(), tabuleiro.getF3Label(),
                tabuleiro.getG1Label(), tabuleiro.getG2Label(), tabuleiro.getG3Label(),
                tabuleiro.getP1Label(), tabuleiro.getP2Label(), tabuleiro.getP3Label(),
                tabuleiro.getP4Label(), tabuleiro.getP5Label(), tabuleiro.getP6Label(),
                tabuleiro.getP7Label(), tabuleiro.getP8Label(), tabuleiro.getP9Label());
         
         for(JLabel casa : pecas){
             casa.removeMouseListener(listenerM);
         }
    }    
}
