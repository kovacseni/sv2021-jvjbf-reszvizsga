package cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private List<Movie> movies = new ArrayList<>();
    private ModelMapper modelMapper;
    private AtomicLong idGenerator = new AtomicLong();

    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<MovieDTO> getMovies(Optional<String> title) {
        Type targetListType = new TypeToken<List<MovieDTO>>() {
        }.getType();
        List<Movie> filteredMovies = movies.stream()
                .filter(movie -> title.isEmpty() || movie.getTitle().equalsIgnoreCase(title.get()))
                .collect(Collectors.toList());
        return modelMapper.map(filteredMovies, targetListType);
    }

    public MovieDTO getExactMovieById(long id) {
        Movie movie = findById(id);

        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGenerator.incrementAndGet(), command.getTitle(), command.getDate(), command.getMaxSpaces(), 0);
        movie.setFreeSpaces(command.getMaxSpaces());
        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO reserve(long id, CreateReservationCommand command) {
        Movie movie = findById(id);
        movie.setFreeSpacesAfterReservation(command.getSpaces());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO updateDate(long id, UpdateDateCommand command) {
        Movie movie = findById(id);
        movie.setDate(command.getNewDate());
        return modelMapper.map(movie, MovieDTO.class);
    }

    private Movie findById(long id) {
        return movies.stream()
                .filter(movie -> movie.getId().longValue() == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such movie with id = " + id));
    }

    public void deleteAllMovies() {
        movies.clear();
        idGenerator = new AtomicLong();
    }
}
