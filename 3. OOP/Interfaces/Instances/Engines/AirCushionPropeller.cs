using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Engines
{
    internal class AirCushionPropeller : IEngine
    {
        string name;
        int power;
        ICheck check;

        public AirCushionPropeller(string name, ICheck check)
        {
            this.check = check;
            this.name = name;
        }
        public string GetName()
        {
            return name;
        }

        public int GetPower()
        {
            return power;
        }

        public string GetStatus()
        {
            return "";
        }

        public void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "power" });
            power = (int)args["power"];
        }

        public int GetMaxPower()
        {
            return Int32.MaxValue;
        }

        public int GetMinPower()
        {
            return Int32.MinValue;
        }
    }
}
