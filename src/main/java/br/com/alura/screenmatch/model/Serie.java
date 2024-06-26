package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.principal.UIcolor;
import br.com.alura.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id //indica que é uma variavel ID
    @GeneratedValue(strategy = GenerationType.IDENTITY) //informa como que é gerado as chaves primarias
    private long id;
    @Column(unique = true) //indica que o titulo devera ser unico nao pode ser repetidos
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;

    @Enumerated(EnumType.STRING) //indica o tipo de enum que é
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;
    //@Transient // indica que nao precisa ser criado
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();


    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this)); //"essa serie é dona desse episodio"
        this.episodios = episodios;
    }

    public Serie() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return UIcolor.ANSI_RED + "Serie{" + UIcolor.ANSI_RESET +
                UIcolor.ANSI_BLUE + "\nGenero: " + UIcolor.ANSI_RESET + genero +
                UIcolor.ANSI_BLUE + "\nTitulo: " + UIcolor.ANSI_RESET + titulo +
                UIcolor.ANSI_BLUE + "\nTotalTemporadas: " + UIcolor.ANSI_RESET + totalTemporadas +
                UIcolor.ANSI_BLUE + "\nAvaliacao: " + UIcolor.ANSI_RESET + avaliacao +
                UIcolor.ANSI_BLUE + "\nAtores=: " + UIcolor.ANSI_RESET + atores +
                UIcolor.ANSI_BLUE + "\nPoster=: " + UIcolor.ANSI_RESET + poster +
                UIcolor.ANSI_BLUE + "\nSinopse: " + UIcolor.ANSI_RESET + sinopse +
                UIcolor.ANSI_BLUE + "\nEpsodios: " + UIcolor.ANSI_RESET + episodios +

                UIcolor.ANSI_RED + '}' + UIcolor.ANSI_RESET;
    }

}
