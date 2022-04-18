using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Doors
{
    internal class BaseDoor : IDoor
    {
        string name;
        Directions direction;
        protected ICheck check;

        public BaseDoor(string name, ICheck check)
        {
            this.name = name;
            this.check = check;
        }

        public Directions GetDirection()
        {
            return direction;
        }

        public string GetName()
        {
            return name;
        }

        public string GetStatus()
        {
            return "";
        }

        public virtual void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[] { "direction" });
            direction = (Directions)args["direction"];
        }
    }
}
