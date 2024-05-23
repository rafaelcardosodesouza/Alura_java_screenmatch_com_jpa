package br.com.alura.screenmatch.repository;


import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String autorSerie, Double avalicao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int temporadas, double avaliacao);


    /**
    * Passa quantidade de temporadas e o avaliacao por para consulta no banco de dado
     * via query
    *  @:temporadas = temporadas passada por paramentro
    *  @:avaliacao = avaliacao passado por parametro
    * */
    @Query("select s from   Serie s where s.totalTemporadas <= :temporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int temporadas, double avaliacao);
}


