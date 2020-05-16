package com.gwidgets.mongotest;

import java.util.stream.Stream;

public class People
{
    public static Stream<Person> stream() {

        return Stream.of("Walter White", "Skyler White", "Jesse Pinkman", "Hank Schrader", "Marie Schrader",
                "Walter White, Jr.", "Saul Goodman", "Gustavo Fring", "Mike Ehrmantraut", "Lydia Rodarte-Quayle",
                "Todd Alquist", "Kimberly Wexler", "Howard Hamlin", "Ignacio 'Nacho' Varga", "Charles 'Chuck' McGill, Jr.",
                "Steven Gomez", "Skinny Pete", "Carmen Molina", "Tuco Salamanca", "Gretchen Schwartz", "Gonzo", "No-Doze",
                "Domingo 'Krazy-8' Molina", "Emilio Koyama", "Brandon 'Badger' Mayhew", "Christian 'Combo' Ortega",
                "Adam Pinkman", "Wendy", "Bogdan Wolynetz", "Elliott Schwartz", "Ken 'Ken Wins'", "Holly White", "Ted Beneke",
                "George Merkert", "Hector Salamanca", "Jane Margolis", "Donald Margolis", "Clovis", "SAC Ramey", "Victor",
                "Tomás Cantillo", "Francesca Liddy", "Cythia", "Tortuga", "Det. Tim Roberts", "Andrea Cantillo",
                "Brock Cantillo", "Gale Boetticher", "Leonel Salamanca", "Juan Bolsa", "Group Leader", "Kaylee Ehrmantraut",
                "Marco Salamanca", "Pamela", "Duane Chow", "Stacey Ehrmantraut", "Officer Saxton", "Huell Babineaux",
                "Patrick Kuby", "Chris Mara", "Tyrus Kitt", "Don Eladio Vuente", "Gaff", "Dennis Markowski", "Lawson",
                "Barry Goodman", "Detective Kalanchoe", "Detective Munn", "Nurse", "Stephanie Doswell", "Declan",
                "Ron Forenall", "Dan Wachsberger", "Jack Welker", "Kenny", "Frankie", "Fran", "Lester", "Matt", "Ernesto",
                "Mrs. Nguyen", "Rick Schweikart", "Dr. Caldera", "Bill Oakley", "Irene Landry", "Marco Pasternak",
                "Betsy Kettleman", "Craig Kettleman", "Detective Sanders", "Detective Abbasi", "Joey Dixon", "Sound Guy",
                "Daniel 'Pryce' Wormald", "Mrs. Strauss", "Brenda", "Dr. Laura Cruz", "Paige Novick", "Arturo",
                "Kevin Wachtell", "Erin Brill", "Ximenez Lecerda", "Clifford Main", "Omar", "Brian Archuleta",
                "Rebecca McGill née Bois", "Make-up Artist", "Captain Bauer", "Mr. Ughetta", "David Brightbill")
                .map(Person::new);
    }
}
