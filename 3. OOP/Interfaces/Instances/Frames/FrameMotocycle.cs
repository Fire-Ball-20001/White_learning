using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances.Frames
{
    internal class FrameMotocycle : Frame
    {
        public FrameMotocycle(string name, ICheck check)
            : base(name,countDoors: 0, countWheels:2,check){}


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
