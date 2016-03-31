/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import model.Player;
import negocio.Tabuleiro;

/**
 *
 * @author Joamila
 * Created: 11/03/2016
 * Last Modified: 20/03/2016
 */
public class Trilha extends UnicastRemoteObject implements TrilhaInterface{
    
    private Map<Integer, Player> jogadores; 
    Tabuleiro tabuleiroMetod = new Tabuleiro();
    ArrayList<String> ultimaJogada;
    int idUltimoJogador;
    String pecaExcluida = "";
    int respostaReinicio;
    int idUltimoChat;
    String mensagemChat="";
    
    //Flags
    int flagJogadaEnviada;
    int flagPecaExcluida;
    int flagMoinho;
    int flagVitoria;
    int flagEmpate;
    int flagVoltarJogo;
    int flagSolicReinicio;
    int flagRespostaReinicio;
    int flagDesistencia;
    int flagMensagem;
    int flagFecharJanela;
    
    public void setJogadores(Map<Integer,Player> jogadores){ this.jogadores = jogadores;}
      
    public Trilha() throws RemoteException{
      super();
      this.jogadores = new HashMap();
      this.ultimaJogada = new ArrayList();
      this.flagJogadaEnviada = 0;
      this.flagPecaExcluida = 0;
      this.flagMoinho = 0;
      this.flagVitoria = 0;
      this.flagEmpate = 0;
      this.flagVoltarJogo = 0;
      this.flagSolicReinicio = 0;
      this.flagRespostaReinicio = 0;
      this.flagDesistencia = 0;
      this.flagMensagem = 0;
      this.flagFecharJanela = 0;
    }
    
    /** Atribui um id único para cada jogador
     * 
     * @param nome String
     * @return int
     * @throws RemoteException 
     */
    @Override
    public int conectarCliente(String nome) throws RemoteException {
        int idAux = jogadores.size();
        
        jogadores.put(idAux, new Player());
        jogadores.get(idAux).setId(idAux);
        jogadores.get(idAux).setNome(nome);
        jogadores.get(idAux).setCor("");
        
        System.out.println(jogadores.get(idAux).getId() + " - " + jogadores.get(idAux).getNome() + " se conectou.");
     
        return idAux;
    }
    
    /** Verifica se há um adversário disponível para o jogo
     * 
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkAdversarios() throws RemoteException {
        return jogadores.size()>1;
    }
    
    /** Define as cores dos tabuleiros
     * 
     * @param id int
     * @return String
     * @throws RemoteException 
     */
    @Override
    public String definirCor(int id) throws RemoteException {
        String cor = "azul";
        jogadores.get(id).setCor(cor);
        
        for(int i=0; i<jogadores.size(); i++){
            if(id != jogadores.get(i).getId()){
                jogadores.get(i).setCor("vermelha");
            }
        }
        return cor;
    }
    
    /** Informa a cor das peças do jogador
     * 
     * @param id int
     * @return String
     * @throws RemoteException 
     */
    @Override
    public String getCorPecas(int id) throws RemoteException {
        return jogadores.get(id).getCor();
    }
    
    /**Envia uma jogada para o adversário
     * 
     * @param id int
     * @param casaInicial String
     * @param casaFinal String
     * @throws RemoteException 
     */
    @Override
    public void enviarJogada(int id, String casaInicial, String casaFinal) throws RemoteException {
        idUltimoJogador = id;
        flagJogadaEnviada = 1;
        ultimaJogada.add(0, casaInicial);
        ultimaJogada.add(1, casaFinal);
    }
    
    /** Verifica se uma jogada foi efetuada
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkUltimaJogada(int id) throws RemoteException {
        return !(idUltimoJogador == id || flagJogadaEnviada == 0);
    }
    
    /**Recebe uma jogada: casa inicial e final
     * 
     * @return ArrayList
     * @throws RemoteException 
     */
    @Override
    public ArrayList<String> receberJogada() throws RemoteException { 
        flagJogadaEnviada = 0;
        return ultimaJogada;
    } 
    
    /**Informa que foi feito um moinho
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void informarMoinho(int id) throws RemoteException {
        idUltimoJogador = id;
        flagMoinho = 1;
    }
    
    /**Verifica se um moinho foi feito
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkMoinho(int id) throws RemoteException {
        if(!(idUltimoJogador == id || flagMoinho == 0)){
            flagMoinho = 0;
            return true;
        }
        else{
            return false;
        }
    }
    
    /**Informa que uma peça deve ser excluída
     * 
     * @param id int
     * @param casa String
     * @throws RemoteException 
     */
    @Override
    public void excluirPeca(int id, String casa) throws RemoteException {
        idUltimoJogador = id;
        flagPecaExcluida = 1;
        pecaExcluida = casa;
    }
    
    /**Verifica se alguma peça foi excluída
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkPecaExcluida(int id) throws RemoteException {
        return !(idUltimoJogador == id || flagPecaExcluida == 0);
    }
    
    /**Recebe informação sobre a peça excluída
     * 
     * @return String
     * @throws RemoteException 
     */
    @Override
    public String receberExclusao() throws RemoteException {
        flagPecaExcluida = 0;
        return pecaExcluida;
    }

    /**Informa que houve vitória
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void informarVitoria(int id) throws RemoteException {
        idUltimoJogador = id;
        flagVitoria = 1;
    }
    
    /**Verfica se houve uma vitória
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkVitoriaAdv(int id) throws RemoteException {
        return !(idUltimoJogador == id || flagVitoria == 0); 
    }
    
    /**Informa que houve um empate
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void informarEmpate(int id) throws RemoteException {
        idUltimoJogador = id;
        flagEmpate = 1;
    }
    
    /**Verifica se houve um empate
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkEmpate(int id) throws RemoteException {
        return !(idUltimoJogador == id || flagEmpate == 0);
    }

    /**Informa que uma jogada voltou
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void voltarJogo(int id) throws RemoteException {
        idUltimoJogador = id;
        flagVoltarJogo = 1;
    }

    /**Verifica se a jogada voltou
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkVoltarJogo(int id) throws RemoteException {
        if(!(idUltimoJogador == id || flagVoltarJogo == 0)){
            flagVoltarJogo = 0;
            return true;
        }
        else{
            return false;
        }
    }
    
    /**Solicita o reinício do jogo
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void solicitarReinicio(int id) throws RemoteException {
        idUltimoJogador = id;
        flagSolicReinicio = 1;
    }

    /**Verifica se o reinício do jogo foi solicitado
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkSolicReinicio(int id) throws RemoteException {
        if(!(idUltimoJogador == id || flagSolicReinicio == 0)){
            flagSolicReinicio = 0;
            return true;
        }
        else{
            return false;
        }
    }

    /**Responde a solicitação de reinício de jogo
     * 
     * @param id int
     * @param resposta int
     * @throws RemoteException 
     */
    @Override
    public void responderSolicitacaoReinicio(int id, int resposta) throws RemoteException {
        idUltimoJogador = id;
        flagRespostaReinicio = 1;
        respostaReinicio = resposta;
    }

    /**Verifica se há uma resposta para a solicitação de reinício de jogo
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkRespostaReinicio(int id) throws RemoteException {
        if(!(idUltimoJogador == id || flagRespostaReinicio == 0)){
            flagRespostaReinicio = 0;
            return true;
        }
        else{
            return false;
        }
    }
    
    /**Recebe uma resposta para a solicitação de reinício de jogo
     * 
     * @return int
     * @throws RemoteException 
     */
    @Override
    public int receberRespostaReinicio() throws RemoteException {
        if(respostaReinicio == JOptionPane.OK_OPTION){
            this.flagJogadaEnviada = 0;
            this.flagPecaExcluida = 0;
            this.flagMoinho = 0;
            this.flagVitoria = 0;
            this.flagEmpate = 0;
            this.flagVoltarJogo = 0;
            this.flagSolicReinicio = 0;
            this.flagDesistencia = 0;
        }
        
        flagRespostaReinicio = 0;
        return respostaReinicio;
    }

    /**Informa a desistência
     * 
     * @param id int
     * @throws RemoteException 
     */
    @Override
    public void desistirJogo(int id) throws RemoteException {
        idUltimoJogador = id;
        flagDesistencia = 1;
    }
    
    /**Verifica se houve desistência do jogo
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkDesistencia(int id) throws RemoteException {
        return !(idUltimoJogador == id || flagDesistencia == 0);
    }
    
    /**Informa que uma janela foi fechada
     * 
     * @throws RemoteException 
     */
    @Override
    public void fecharJanela() throws RemoteException {
        flagFecharJanela = 1;
    }
    
    /**Verifica se alguma janela foi fechada
     * 
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkFecharJanela() throws RemoteException {
        return flagFecharJanela == 1;
    }

    /**Envia mensagem pelo chat
     * 
     * @param id int
     * @param mensagem String
     * @throws RemoteException 
     */
    @Override
    public void enviarMensagemChat(int id, String mensagem) throws RemoteException {
        idUltimoChat = id;
        flagMensagem = 1;
        mensagemChat= mensagem;
    }
    
    /**Verifica se há uma mensagem nova no chat
     * 
     * @param id int
     * @return boolean
     * @throws RemoteException 
     */
    @Override
    public boolean checkMensagem(int id) throws RemoteException {
       return !(idUltimoChat == id || flagMensagem == 0);
    }

    /**Recebe uma mensagem nova pelo chat
     * 
     * @return String
     * @throws RemoteException 
     */
    @Override
    public String receberMensagemChat() throws RemoteException {
        flagMensagem = 0;
        String nome = jogadores.get(idUltimoChat).getNome();
        return nome + ": " + mensagemChat;
    }  
}
