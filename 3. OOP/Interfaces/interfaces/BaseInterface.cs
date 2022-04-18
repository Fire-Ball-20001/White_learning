using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface BaseInterface
    {

        string GetName();
        void init(IDictionary<String,Object> args);
        string GetStatus();

    }
}
