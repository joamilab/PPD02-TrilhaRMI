/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Joamila
 * Created: 11/03/2016
 * Last Modified: 20/03/2016
 */
public interface TrilhaInterface extends Remote{
    public int conectarCliente(String nome) throws RemoteException;
    public boolean checkAdversarios() throws RemoteException;
    
    public String definirCor(int id) throws RemoteException;
    public String getCorPecas(int id) throws RemoteException;
    
    public boolean checkUltimaJogada(int id) throws RemoteException;
    public void enviarJogada(int id, String casaInicial, String casaFinal) throws RemoteException;
    public ArrayList<String> receberJogada() throws RemoteException;
    
    public void informarMoinho(int id) throws RemoteException;
    public boolean checkMoinho(int id) throws RemoteException;
    
    public void excluirPeca(int id, String casa) throws RemoteException;
    public boolean checkPecaExcluida(int id) throws RemoteException;
    public String receberExclusao() throws RemoteException;
    
    public void informarVitoria(int id) throws RemoteException;
    public boolean checkVitoriaAdv(int id) throws RemoteException;
    
    public void informarEmpate(int id) throws RemoteException;
    public boolean checkEmpate(int id) throws RemoteException;
    
    public void voltarJogo(int id) throws RemoteException;
    public boolean checkVoltarJogo(int id) throws RemoteException;
    
    public void solicitarReinicio(int id) throws RemoteException;
    public boolean checkSolicReinicio(int id) throws RemoteException;
    public void responderSolicitacaoReinicio(int id, int resposta) throws RemoteException;
    public int receberRespostaReinicio() throws RemoteException;
    public boolean checkRespostaReinicio(int id) throws RemoteException;
    
    public void desistirJogo(int id) throws RemoteException;
    public boolean checkDesistencia(int id) throws RemoteException;
    
    public void fecharJanela() throws RemoteException;
    public boolean checkFecharJanela() throws RemoteException; 
    
    public void enviarMensagemChat(int id, String mensagem) throws RemoteException;
    public boolean checkMensagem(int id) throws RemoteException;
    public String receberMensagemChat() throws RemoteException;
}
