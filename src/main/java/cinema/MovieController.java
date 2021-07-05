package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cinema")
public class MovieController {

    private MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return service.getMovies(title);
    }

    @GetMapping("/{id}")
    public MovieDTO getExactMovieById(@PathVariable long id) {
        return service.getExactMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command) {
        return service.createMovie(command);
    }

    @PostMapping("/{id}/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO reserve(@PathVariable long id, @RequestBody CreateReservationCommand command) {
        return service.reserve(id, command);
    }

    @PutMapping("/{id}")
    public MovieDTO updateDate(@PathVariable long id, @RequestBody UpdateDateCommand command) {
        return service.updateDate(id, command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMovies() {
        service.deleteAllMovies();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleNotFoundWithIdGiven(IllegalArgumentException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleBadRequestAtReserving(IllegalStateException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Bad reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("cinema/validation-error"))
                .withTitle("Validation error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violation", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }
}
