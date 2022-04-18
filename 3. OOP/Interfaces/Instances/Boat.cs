using Interfaces.interfaces;

namespace Interfaces.Instances
{
    internal class Boat : IMove
    {
        IBottom bottom;
        IEngine engine;
        string name;

        public Boat(string name,IBottom bottom,IEngine engine)
        {
            this.bottom = bottom;
            this.engine = engine;
            this.name = name;
        }

        public string GetName()
        {
            return name;
        }

        public string Move(string[] initMessages)
        {
            throw new System.NotImplementedException();
        }

        public bool[] Status(string[] initMessages)
        {
            throw new System.NotImplementedException();
        }
    }
}
