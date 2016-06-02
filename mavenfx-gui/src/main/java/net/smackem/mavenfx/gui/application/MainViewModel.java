package net.smackem.mavenfx.gui.application;

public class MainViewModel {
    private final BoardViewModel boardViewModel = new BoardViewModel();

    public BoardViewModel getBoardViewModel() {
        return this.boardViewModel;
    }
}
