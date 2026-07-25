package com.sddevops.jenkins_maven.eclipse;

import java.util.Comparator;
import java.util.Objects;

public class Song {
	private String id;
	private String title;
	private String artiste;
	private double songLength;

	/**
	 * @param id
	 * @param title
	 * @param artiste
	 * @param songLength
	 */
	
	//Constructor
	public Song(String id, String title, String artiste, double songLength) {
		super(); //inheritance,no need to reinvent
		this.id = id;
		this.title = title;
		this.artiste = artiste;
		this.songLength = songLength;
	}

	/**
	 * @return the id
	 */
	//Getter
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	//Setter
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the artiste
	 */
	public String getArtiste() {
		return artiste;
	}

	/**
	 * @param artiste the artiste to set
	 */
	public void setArtiste(String artiste) {
		this.artiste = artiste;
	}

	/**
	 * @return the songLength
	 */
	public double getSongLength() {
		return songLength;
	}

	/**
	 * @param songLength the songLength to set
	 */
	public void setSongLength(double songLength) {
		this.songLength = songLength;
	}

	@Override
	public int hashCode() {
		return Objects.hash(artiste, id, songLength, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Song))
			return false;
		Song other = (Song) obj;
		return Objects.equals(artiste, other.artiste) && Objects.equals(id, other.id)
				&& Double.doubleToLongBits(songLength) == Double.doubleToLongBits(other.songLength)
				&& Objects.equals(title, other.title);
	}

	public static final Comparator<Song> titleComparator =
	        (s1, s2) -> s1.getTitle().compareTo(s2.getTitle());

	public static final Comparator<Song> songLengthComparator =
	        (s1, s2) -> Double.compare(s2.getSongLength(), s1.getSongLength());

@Override
public String toString() {
    return title + " by " + artiste;
}
}
