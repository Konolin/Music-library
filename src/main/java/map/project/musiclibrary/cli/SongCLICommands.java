package map.project.musiclibrary.cli;

import map.project.musiclibrary.data.model.audios.Song;
import map.project.musiclibrary.data.model.users.Admin;
import map.project.musiclibrary.data.model.users.ArtistUser;
import map.project.musiclibrary.data.model.users.NormalUser;
import map.project.musiclibrary.data.model.users.UserSession;
import map.project.musiclibrary.service.ArtistUserService;
import map.project.musiclibrary.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@ShellComponent
public class SongCLICommands {
    private final SongService songService;
    private final UserSession userSession;

    @Autowired
    public SongCLICommands(SongService songService, UserSession userSession) {
        this.songService = songService;
        this.userSession = userSession;
    }

    @ShellMethod(key = "listSongs", value = "List all songs")
    public String listSongs() {
        if (userSession.isLoggedIn()) {
            return songService.findAll().toString();
        } else {
            return "You must be logged in to se all songs";
        }
    }

    @ShellMethod(key = "addSong", value = "Add a song")
    public String addSong(@ShellOption(value = {"name"}, help = "Name of the song") final String name,
                          @ShellOption(value = {"genre"}, help = "Genre of the song") final String genre,
                          @ShellOption(value = {"length"}, help = "Length of the song(in seconds)") final String lengthStr,
                          @ShellOption(value = {"releaseDate"}, help = "The release date of the song") final String releaseDateStr,
                          @ShellOption(value = {"artistId"}, help = "The id of the artist of the song") final String artistIdStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof Admin) {
            try {
                return songService.addSong(name, genre, lengthStr, releaseDateStr, artistIdStr).toString();
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            } catch (ParseException e) {
                return "Invalid birthdate format. Please use yyyy-MM-dd.";
            }
        } else {
            return "Only admin can add songs";
        }
    }

    @ShellMethod(key = "findSong", value = "Find a song by name")
    public String findSong(@ShellOption(value = {"name"}, help = "Name of the song") final String name) {
        return songService.findByName(name).toString();
    }

    // TODO - play song by name ca nu are sens cu id
    @ShellMethod(key = "playSong", value = "Play a song by ID")
    public String playSong(@ShellOption(value = {"songId"}, help = "ID of the song") final String songIdStr) {
        if (userSession.isLoggedIn() && userSession.getCurrentUser() instanceof NormalUser) {
            return songService.playSong(songIdStr, (NormalUser) userSession.getCurrentUser());
        }
        return "Only normal users can play songs";
    }
}
