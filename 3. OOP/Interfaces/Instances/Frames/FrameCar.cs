using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Frames
{
    internal class FrameCar : Frame
    {
        public FrameCar(string name, ICheck check)
            : base(name, countDoors:2,countWheels:3,  check , doorsAndWheelsUnlimited:true) { }

        public override void init(IDictionary<string, object> args)
        {
            check.CheckDictionary(args, new string[]
            {
                "wheels",
                "doors"
            });
            base.init(args);
        }
    }
}
