/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Joamila
 * Created: 15/03
 * Last modified: 20/03
 */
public class Player {
    private int id;
    private String cor;
    private String nome;
    
    //Flags 
    int flagCorPecas;
    int flagJogadaEncerrada;
    int flagTrilha;
    boolean flagAtiva;
    boolean flagAdversario;
    boolean flagExclusao;
    
    //Getters e setters
    public int getId(){ return this.id; }
    public void setId(int id) { this.id = id; }
    public String getCor(){ return this.cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getNome(){ return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getFlagCor(){return flagCorPecas;}
    public void setFlagCor(int flagCorPecas){this.flagCorPecas = flagCorPecas;}
    public int getFlagJogada(){return flagJogadaEncerrada;}
    public void setFlagJogada(int flagJogadaEncerrada){this.flagJogadaEncerrada = flagJogadaEncerrada;}
    public int getFlagTrilha(){return flagTrilha;}
    public void setFlagTrilha(int flagTrilha){this.flagTrilha = flagTrilha;}
    public boolean getFlagAtiva(){return flagAtiva;}
    public void setFlagAtiva(boolean flagAtiva){this.flagAtiva = flagAtiva;}
    public boolean getFlagAdversario(){return flagAdversario;}
    public void setFlagAdversario(boolean flagAdversario){this.flagAdversario = flagAdversario;}
    public boolean getFlagExclusao(){return this.flagExclusao;}
    public void setFlagExclusao(boolean flagExclusao){this.flagExclusao = flagExclusao;}
    
    public Player(){
        this.flagCorPecas = 0;
        this.flagJogadaEncerrada = 0;
        this.flagTrilha = 0;
        this.flagAtiva = true;
        this.flagAdversario = true;
        this.flagExclusao = false;
    }
}
