package jim.journal;

public abstract class Command {
    
    // execute() does not need to have any arguments (per se).
    // This implies that each instance of Command refers to a specific command
    //   e.g. "add lunch Monday"
    // rather than a family of commands (e.g. new Command(add).executeWithArgs("lunch Monday"))
    //
    // execute does, however, have a JournalManager as an argument;
    // since Command interface connects language processing of SuggestionManager
    // with Journal logic, we need some way to pass the Journal context to
    // a generated command.
    // Thus, inject the dependency here,
    // OR inject the dependency in the instance of SuggestionManager,
    // OR have a Singleton of the managers.. (See http://en.wikipedia.org/wiki/Singleton_pattern).
    //
    // I chose the first option there.
    public abstract void execute(JournalManager journalManager);
    public abstract String addAnEvent(String anEvent);
    public abstract String deleteAnEvent();
}
