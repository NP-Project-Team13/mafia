package mafia;

import mafia.character.Character;

public class Player {
    private String name;
    private String team;
    private Character character;

    public Player(String name, Character character) {
        this.name = name;
        this.team = character.getTeam();
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return name + " (Team: " + getTeam() + ") - " + character.getName();
    }
}