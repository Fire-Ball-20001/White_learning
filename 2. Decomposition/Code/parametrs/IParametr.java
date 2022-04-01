

public interface IParametr {

    boolean init(ICommandSender sender, String value);
    Object getValue();
    boolean isOptional();
    CommandBuilder end();
    boolean isDependent();
    String getName();
    String getPerm();
}
