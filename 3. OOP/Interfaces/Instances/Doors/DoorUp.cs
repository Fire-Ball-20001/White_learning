using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;
namespace Interfaces.Instances.Doors
{
    internal class DoorUp : BaseDoor
    {
        public DoorUp(string name, ICheck check)
            : base(name, check) { }


        public override void init(IDictionary<string, object> args)
        {
            args["direction"] = Directions.UP;
            base.init(args);
        }
    }
}
