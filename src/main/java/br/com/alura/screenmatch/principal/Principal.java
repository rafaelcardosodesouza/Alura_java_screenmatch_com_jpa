package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.principal.UIcolor;
import ch.qos.logback.core.encoder.JsonEscapeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {


    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=603e2e61";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries 
                    4 - Buscar série por titulo
                    5 - Buscar serie por autor    
                    6 - Top 5 series        
                    7 - Buscar por categoria
                    8 - Buscar por quantidade de temporada e avaliação
                        
                    0 - Sair                                 
                    """;
            System.out.print(menu);
            System.out.print(UIcolor.ANSI_RED + "--> " + UIcolor.ANSI_RESET);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAutor();
                    break;
                case 6:
                    topCinco();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscaPorTemporadaEAvaliacao();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados); // salvamos os dados no objeto serie
        //dadosSeries.add(dados);

        repositorio.save(serie); //.save é do proprio springBoot
        System.out.println(dados);

    }

    private DadosSerie getDadosSerie() {
        System.out.print(UIcolor.ANSI_BLUE + "Digite o nome da série para busca: " + UIcolor.ANSI_RESET);
        var nomeSerie = leitura.nextLine().toLowerCase();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Qual uma serie pelo o nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }


    private void listarSeriesBuscadas() {
        series = repositorio.findAll(); //findAll() é do springboot
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o Da serie: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("Dados da serie: " + serieBuscada.get());
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriePorAutor() {
        System.out.print("Digite o nome do Autor: ");
        var autorSerie = leitura.nextLine();
        System.out.print("Digite a media de avaliação para ser buscado só series que tenha no minimo a avaliação informada: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEcontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(autorSerie, avaliacao);
        seriesEcontradas.forEach(s -> System.out.println("Series em que " + UIcolor.ANSI_RED + autorSerie + UIcolor.ANSI_RESET + " Trabalhou: " + UIcolor.ANSI_BLUE + s.getTitulo() + UIcolor.ANSI_RESET + " Avaliação, " + s.getAvaliacao()));
    }

    private void topCinco() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> System.out.println(UIcolor.ANSI_BLUE + s.getTitulo() + UIcolor.ANSI_RESET + " Avaliação, " + UIcolor.ANSI_RED + s.getAvaliacao() + UIcolor.ANSI_RESET));
    }

    private void buscarSeriePorCategoria() {
        System.out.print("Digite o genero que deseja: ");
        var genero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(genero);

        List<Serie> buscaGenero = repositorio.findByGenero(categoria);
        System.out.println("Series por categoria: " + UIcolor.ANSI_RED + genero + UIcolor.ANSI_RESET);
        buscaGenero.forEach(s -> System.out.println(UIcolor.ANSI_BLUE + s.getTitulo() + UIcolor.ANSI_RESET));
    }

    private void buscaPorTemporadaEAvaliacao(){
        System.out.print("Quantas tempordas: ");
        var temporadas = leitura.nextInt();
        System.out.print("Qual avaliação minima: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> busca = repositorio.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);
        System.out.println("Resultado: ");
        busca.forEach(s -> System.out.println(UIcolor.ANSI_BLUE + s.getTitulo() + UIcolor.ANSI_RESET));
    }

}