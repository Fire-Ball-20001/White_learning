using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.Instances
{
    struct Part{
        public string Name { get; private set; }
        public Type Type { get; private set; }

        public Part(string name, Type type)
        {
            Name = name;
            Type = type;
        }
    }
}
